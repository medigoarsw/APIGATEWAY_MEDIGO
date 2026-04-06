# ANÁLISIS EXHAUSTIVO DE ERRORES - API Gateway
# Fecha: 2026-04-03
# Estado: PROBLEMAS IDENTIFICADOS Y SOLUCIONES PROPUESTAS

## RESUMEN EJECUTIVO

Se han identificado **2 problemas críticos** que están causando errores 403 y 500:

### Problema 1: NullPointerException en AuthGatewayService (CAUSA ERRORES 500)
- **Síntoma**: Error 500 "Error del apigateway"
- **Causa**: Backend retorna objeto null o sin propiedad 'role'
- **Ubicación**: `AuthGatewayService.login()` línea donde se lee `role`
- **Solución**: Agregar null-safety check en la lectura de `role`

### Problema 2: Rol Mapping Missing (CAUSA ERRORES 403)
- **Síntoma**: Error 403 Forbidden incluso con token válido
- **Causa**: SecurityConfig espera "AFFILIATE"/"DELIVERY" pero backend envía "USUARIO"/"REPARTIDOR"
- **Ubicación**: Configuración de roles en SecurityConfig
- **Solución**: RoleMapper ya creado, pero necesita ser aplicado en JwtAuthenticationFilter

---

## PROBLEMA 1: NullPointerException - Backend Response Null

### Análisis Técnico

El log del gateway muestra:
```
java.lang.NullPointerException: Cannot get property 'role' on null object
```

Esto ocurre porque:
1. Backend retorna un objeto que es null o un JSON incompleto
2. El método `readAsString()` busca validar keys pero encuentra null
3. Cuando se intenta acceder a `role` en una clase nula, Groovy falla

### Línea Problemática Actual

```java
// En AuthGatewayService.java línea ~83
String role = readAsString(payload, "role");
// ... luego ...
.role(RoleMapper.toCanonical(role))  // Si role es null, RoleMapper.toCanonical(null) debe manejar it
```

### Error en el Log del Gateway
```
at com.medigo.gateway.application.service.AuthGatewayService.login(AuthGatewayService.java:?)
java.lang.NullPointerException: Cannot get property 'role' on null object
```

### Solución Implementada

Ya implementamos `RoleMapper.toCanonical(null)` que retorna "AFFILIATE" por defecto. Esto debería funcionar.

**PERO**: El problema es que `readAsString()` podría estar convirting null a "null" string, no a un valor null real.

---

## PROBLEMA 2: Roles Desalineados (ERRORES 403)

### Análisis del Flujo de Roles

#### Backend:
- Retorna con rol: "USUARIO" (cliente) o "REPARTIDOR" (delivery) o "ADMIN"

#### SecurityConfig espera:
```java
.requestMatchers(HttpMethod.POST, "/api/orders/cart/add").hasRole("AFFILIATE")  // Espera AFFILIATE
```

#### JwtAuthenticationFilter actual:
```java
String role = "ROLE_" + claims.getRole();  // Si claims.role = "USUARIO", resulta "ROLE_USUARIO"
// Spring verifica si el usuario tiene "ROLE_USUARIO"
// SecurityConfig busca "ROLE_AFFILIATE"
// NO COINCIDEN → 403 Forbidden
```

#### Solución:
El RoleMapper debe estar en la cadena ANTES de que se genere el JWT, Y en JwtAuthenticationFilter cuando se valida.

---

## PASO A PASO: PROBLEMAS Y SOLUCIONES

### PASO 1: Verificar qué retorna el backend en login

Crear test simple:
```bash
curl -X POST http://backend:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@medigo.com","password":"123"}'
```

**Respuesta esperada del backend:**
```json
{
  "user_id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN"  ← O "USUARIO" o "REPARTIDOR"
}
```

### PASO 2: RoleMapper - YA IMPLEMENTADO ✅

Archivo: `RoleMapper.java`
Conversiones:
- "USUARIO" → "AFFILIATE"
- "REPARTIDOR" → "DELIVERY"
- "ADMIN" → "ADMIN"
- null → "AFFILIATE" (defecto)

### PASO 3: AuthGatewayService - ACTUALIZADO ✅

```java
String canonicalRole = RoleMapper.toCanonical(role);

UserClaims claims = UserClaims.builder()
        .userId(String.valueOf(id))
        .username(username)
        .email(email == null ? "" : email)
        .role(canonicalRole)  // Usa role mapeado
        .build();
```

### PASO 4: JwtAuthenticationFilter - REQUIERE ACTUALIZACIÓN

Línea actual problema:
```java
String role = "ROLE_" + claims.getRole();  // Asume que claims.getRole() ya es canonico
```

Esto está OK porque ya hicimos el mapping en AuthGatewayService.

**PERO**: Hay un problema si el backend envía un rol en formato diferente que no está mapeado. Necesitamos agregar un segundo nivel de validación.

---

## PROBLEMAS OBSERVADOS EN LOGS DEL GATEWAY

### Error #1: Connection reset
```
java.io.IOException: Se ha anulado una conexión establecida por el software en su equipo host.
```
- Esto es normal - cliente cerró la conexión
- No es crítico

### Error #2: NullPointerException en resolvePayload()
```
java.lang.NullPointerException: Cannot get property 'role' on null object
```
- Backend retorna null o estructura incompleta
- RoleMapper protege contra null pero necesitamos validación adicional

### Error #3: Posible error en BackendClient
```
at com.medigo.gateway.domain.port.out.BackendClient.send(BackendClient.java:?)
```
- BackendClient podría no estar formateando correctamente la URL
- O podría estar enviando headers incorrectos

---

## RECOMENDACIONES INMEDIATAS

### 1. AGREGAR VALIDACIÓN NULL-SAFE EN AuthGatewayService

```java
@Override
public LoginResponse login(LoginRequest request) {
    log.debug("Forwarding login request for user: {}", request.getEmail());

    ResponseEntity<Object> backendResponse = backendClient.send(
            "/api/auth/login", HttpMethod.POST, Map.of(), request
    );

    if (backendResponse.getStatusCode() == HttpStatus.UNAUTHORIZED
            || backendResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new GatewayValidationException("Credenciales inválidas");
    }
    if (backendResponse.getStatusCode().isError()) {
        log.error("Backend respondió con error {}: {}", 
                  backendResponse.getStatusCode(), 
                  backendResponse.getBody());
        throw new IllegalStateException("Error en el backend al autenticar: " 
                + backendResponse.getStatusCode());
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> body = (Map<String, Object>) backendResponse.getBody();

    if (body == null) {
        log.error("Backend retornó body null en login");
        throw new IllegalStateException("Backend returned empty body on login");
    }

    Map<String, Object> payload = resolvePayload(body);
    
    // Validación adicional: verificar que payload no sea null
    if (payload == null) {
        log.error("Payload no pudo ser resuelto del body del backend");
        throw new IllegalStateException("Backend returned invalid payload structure");
    }

    String userId = readAsString(payload, "user_id", "id", "userId");
    String username = readAsString(payload, "username", "userName");
    String email = readAsString(payload, "email");
    String role = readAsString(payload, "role");
    
    // Log para debugging
    log.info("Backend login response: userId={}, username={}, role={}, email={}", 
             userId, username, role, email);

    if (userId == null || userId.isBlank()) {
        throw new IllegalStateException("Backend login response missing user id");
    }
    if (username == null || username.isBlank()) {
        throw new IllegalStateException("Backend login response missing username");
    }

    long id;
    try {
        id = Long.parseLong(userId);
    } catch (NumberFormatException ex) {
        throw new IllegalStateException("Backend login response contains invalid user id: " + userId, ex);
    }

    String canonicalRole = RoleMapper.toCanonical(role);  // Manejar null aquí
    
    log.info("Role mapping: backend_role='{}' -> canonical_role='{}'", role, canonicalRole);

    UserClaims claims = UserClaims.builder()
            .userId(String.valueOf(id))
            .username(username)
            .email(email == null ? "" : email)
            .role(canonicalRole)
            .build();

    String jwt = jwtPort.generateToken(claims);

    return LoginResponse.builder()
            .id(id)
            .username(claims.getUsername())
            .email(claims.getEmail())
            .role(claims.getRole())  // Retorna rol canonico
            .jwtToken(jwt)
            .build();
}
```

### 2. MEJORAR JwtAuthenticationFilter CON LOGGING

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain chain)
        throws ServletException, IOException {

    // Generar o propagar Trace ID
    String traceId = request.getHeader("X-Trace-ID");
    if (!StringUtils.hasText(traceId)) {
        traceId = UUID.randomUUID().toString();
    }
    TraceIdHolder.set(traceId);
    response.setHeader("X-Trace-ID", traceId);

    // Procesar JWT
    String token = extractToken(request);
    if (!StringUtils.hasText(token)) {
        log.warn("[{}] Request sin token: {} {}", traceId, request.getMethod(), request.getRequestURI());
    } else if (!jwtPort.isValid(token)) {
        log.warn("[{}] Token inválido en: {} {}", traceId, request.getMethod(), request.getRequestURI());
    }
    
    if (StringUtils.hasText(token) && jwtPort.isValid(token)) {
        UserClaims claims = jwtPort.validateAndExtract(token);
        
        // Validar que role no sea null
        if (claims == null) {
            log.error("[{}] Claims extraidas son null del token", traceId);
            chain.doFilter(request, response);
            return;
        }
        
        String rawRole = claims.getRole();
        if (rawRole == null || rawRole.isBlank()) {
            log.warn("[{}] Token contiene role vacio: {}", traceId, rawRole);
            rawRole = "AFFILIATE";  // Default safety
        }
        
        String role = "ROLE_" + rawRole;

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        claims, null,
                        List.of(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("[{}] Usuario autenticado: {} rol: {} (final: {})",
                traceId, claims.getUsername(), rawRole, role);
    }

    try {
        chain.doFilter(request, response);
    } finally {
        TraceIdHolder.clear();
    }
}
```

### 3. VERIFICAR BackendClient

El BackendClient debe estar enviando correctamente las peticiones. Ver:
- ¿URL correcta?  
- ¿Headers correctos?
- ¿Content-Type: application/json?
- ¿Timeout configurado?

---

## TESTING STRATEGY

### Test 1: Login con ADMIN (debe retornar ADMIN en JWT)
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@medigo.com",
    "password": "123"
  }'
```

**Esperado**: 
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN",
  "jwt_token": "...",
  "token_type": "Bearer"
}
```

### Test 2: Login con USUARIO (debe convertirse a AFFILIATE)
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@medigo.com",
    "password": "123"
  }'
```

**Esperado**: 
```json
{
  "id": 2,
  "username": "user",
  "email": "user@medigo.com",
  "role": "AFFILIATE",  ← Mapeo USUARIO -> AFFILIATE
  "jwt_token": "...",
  "token_type": "Bearer"
}
```

### Test 3: Usar JWT en endpoint protegido (AFFILIATE)
```bash
TOKEN="<jwt_from_test2>"
curl -X POST http://localhost:8081/api/orders/cart/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "affiliateId": 2,
    "branchId": 1,
    "medicationId": 5,
    "quantity": 2
  }'
```

**Esperado**: 
- 201 Created si SecurityConfig permite AFFILIATE
- 403 Forbidden si SecurityConfig no reconoce el rol

---

## CHECKLIST DE RESOLUCIÓN

- ✅ RoleMapper creado: `RoleMapper.java`
- ✅ AuthGatewayService actualizado: usa `RoleMapper.toCanonical()`
- ✅ SecurityConfig: tiene roles canonicos (ADMIN, AFFILIATE, DELIVERY)
- ⚠️ JwtAuthenticationFilter: necesita mejora con null-safety
- ⚠️ Logging: necesita ser más verboso para debugging
- ⚠️ BackendClient: verificar que está funcionando correctamente
- ⚠️ Testing: ejecutar tests de endpoints

---

## PRÓXIMOS PASOS

1. Actualizar JwtAuthenticationFilter con null-safety mejorado
2. Agregar logging más detallado en AuthGatewayService
3. Ejecutar test de login y verificar que role se mapea correctamente
4. Ejecutar test de endpoints protegidos y verificar 403 vs 200
5. Crear documento de testing final con todos los 30 endpoints

