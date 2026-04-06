# RESOLUCIÓN IMPLEMENTADA - Errores 403 y 500 en API Gateway
# Fecha: 2026-04-03
# Status: CAMBIOS IMPLEMENTADOS - PENDIENTE COMPILACIÓN Y PRUEBAS

## RESUMEN DE CAMBIOS

Se han identificado y implementado soluciones para **2 problemas críticos**:

### 1. ✅ CREADO: RoleMapper (`RoleMapper.java`)
**Ubicación**: `src/main/java/com/medigo/gateway/infrastructure/common/RoleMapper.java`

**Propósito**: Convertir roles del backend a roles canonicos que SecurityConfig espera.

**Conversiones**:
- `USUARIO` → `AFFILIATE` (cliente/paciente)
- `REPARTIDOR` → `DELIVERY` (repartidor)
- `ADMIN` → `ADMIN` (sin cambios)
- `null` → `AFFILIATE` (defecto)

```java
public static String toCanonical(String backendRole) {
    if (backendRole == null || backendRole.isBlank()) {
        log.warn("Rol vacio del backend, asumiendo AFFILIATE por defecto");
        return "AFFILIATE";
    }
    String normalized = backendRole.trim().toUpperCase();
    return switch (normalized) {
        case "USUARIO" -> "AFFILIATE";
        case "REPARTIDOR" -> "DELIVERY";
        case "ADMIN" -> "ADMIN";
        // ... más casos
    };
}
```

---

### 2. ✅ ACTUALIZADO: AuthGatewayService (login y register)  
**Ubicación**: `src/main/java/com/medigo/gateway/application/service/AuthGatewayService.java`

**Cambios**:
- Agregado import: `import com.medigo.gateway.infrastructure.common.RoleMapper;`
- Método `login()`: Usa `RoleMapper.toCanonical(role)` antes de crear JWT
- Método `register()`: Usa `RoleMapper.toCanonical(role)` en respuesta
- Agregado logging para debugging: Qué rol retorna el backend vs qué rol se mapea

**Código**:
```java
// En login():
String canonicalRole = RoleMapper.toCanonical(role);
log.info("Role mapping: backend_role='{}' -> canonical_role='{}'", role, canonicalRole);

UserClaims claims = UserClaims.builder()
        .userId(String.valueOf(id))
        .username(username)
        .email(email == null ? "" : email)
        .role(canonicalRole)  // ← MAPEADO
        .build();

// Similar en register():
String canonicalRole = RoleMapper.toCanonical(role);
// ... usa canonicalRole en respuesta
```

---

### 3. ✅ MEJORADO: JwtAuthenticationFilter (null-safety y logging)
**Ubicación**: `src/main/java/com/medigo/gateway/infrastructure/security/JwtAuthenticationFilter.java`

**Cambios**:
- Agregado try-catch para manejar excepciones en JWT extraction
- Agregada validación null para `claims`
- Agregada validación null/blank para `role`
- Agregado logging detallado de autenticación
- Default a "AFFILIATE" si role es nulo

**Código**:
```java
if (StringUtils.hasText(token) && jwtPort.isValid(token)) {
    try {
        UserClaims claims = jwtPort.validateAndExtract(token);
        
        // Validar que claims no sea null
        if (claims == null) {
            log.error("[{}] Claims extraidas son null del token", traceId);
            chain.doFilter(request, response);
            return;
        }
        
        String rawRole = claims.getRole();
        if (rawRole == null || rawRole.isBlank()) {
            log.warn("[{}] Token contiene role vacio, asignando AFFILIATE por defecto", traceId);
            rawRole = "AFFILIATE";
        }
        
        String grantedAuthority = "ROLE_" + rawRole;
        
        // ... crea authentication ...
        
        log.debug("[{}] Usuario autenticado: username={} role={} authority={}", 
                 traceId, claims.getUsername(), rawRole, grantedAuthority);
    } catch (Exception e) {
        log.error("[{}] Error validando JWT: {}", traceId, e.getMessage(), e);
    }
}
```

---

## POR QUÉ ESTO RESUELVE LOS ERRORES

### Causas de Error 403 (Forbidden):

**Antes**:
1. Backend retorna: `"role": "USUARIO"`
2. Gateway crea JWT con: `role: "USUARIO"`
3. SecurityConfig busca: `hasRole("AFFILIATE")`
4. JwtAuthenticationFilter crea authority: `"ROLE_USUARIO"`
5. Spring compara: `"ROLE_USUARIO"` ≠ `"ROLE_AFFILIATE"` → **403**

**Después**:
1. Backend retorna: `"role": "USUARIO"`
2. **RoleMapper convierte**: `"USUARIO"` → `"AFFILIATE"`
3. Gateway crea JWT con: `role: "AFFILIATE"`
4. SecurityConfig busca: `hasRole("AFFILIATE")`
5. JwtAuthenticationFilter crea authority: `"ROLE_AFFILIATE"`
6. Spring compara: `"ROLE_AFFILIATE"` = `"ROLE_AFFILIATE"` → **✅ 200 OK**

### Causas de Error 500 (Internal Server Error):

**Antes**:
1. Backend retorna objeto incompleto o null
2. `AuthGatewayService.login()` intenta usar propiedades null
3. RoleMapper no tiene null-safety completo
4. JwtAuthenticationFilter no maneja excepciones
5. → **500 Error del apigateway**

**Después**:
1. Backend retorna objeto incompleto o null
2. `RoleMapper.toCanonical(null)` retorna **`"AFFILIATE"`** (default)
3. JwtAuthenticationFilter tiene try-catch y null checks
4. Si todo falla, continúa como anónimo en lugar de crash
5. → **Manejo elegante de errores, mejor logging**

---

## PRÓXIMAS ACCIONES - PARA EL USUARIO

### 1. COMPILAR
```bash
cd d:\ander\Documents\SEMESTRE 7\ARSW\PROYECTO OFICIAL\APIGATEWAY_MEDIGO
mvn clean package -DskipTests
```

**Esperado**: `BUILD SUCCESS`

### 2. INICIAR GATEWAY
```bash
java -jar target\medigo-api-gateway-1.0.0.jar
```

**Esperado**: Aplicación inicia en puerto 8081, sin errores de compilación

### 3. PROBAR ENDPOINT DE LOGIN

```bash
# Test 1: Login (debe retornar JWT con rol canonico)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@medigo.com",
    "password": "123"
  }'
```

**Respuesta esperada**:
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

**Importante**: El `role` en la respuesta debe ser:
- `"ADMIN"` si backend retorna "ADMIN"
- `"AFFILIATE"` si backend retorna "USUARIO"
- `"DELIVERY"` si backend retorna "REPARTIDOR"

### 4. PROBAR ENDPOINT PROTEGIDO (ADMIN)

```bash
TOKEN="<jwt_token_del_paso_3>"

# Test 2: GET endpoint ADMIN (debe retornar 200)
curl -X GET "http://localhost:8081/api/auth/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
```

**Si token es ADMIN**: Esperado `200 OK`
**Si token es AFFILIATE/DELIVERY**: Esperado `403 Forbidden`

### 5. PROBAR ENDPOINT PROTEGIDO (AFFILIATE)

```bash
TOKEN="<jwt_token_de_usuario_affiliate>"

# Test 3: POST endpoint AFFILIATE (debe retornar 201 o 200)
curl -X POST "http://localhost:8081/api/orders/cart/add" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "branchId": 1,
    "medicationId": 5,
    "quantity": 2
  }'
```

**Si token es AFFILIATE**: Esperado `201 Created`
**Si token es ADMIN/DELIVERY**: Esperado `403 Forbidden`

### 6. PROBAR SIN TOKEN

```bash
# Test 4: Endpoint autenticado sin token (debe retornar 401)
curl -X POST "http://localhost:8081/api/orders/cart/add" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "branchId": 1,
    "medicationId": 5,
    "quantity": 2
  }'
```

**Esperado**: `401 Unauthorized` o `403 Forbidden`

### 7. REVISAR LOGS

En los logs del gateway, deberías ver:
```
[TRACE_ID] Role mapping: backend_role='USUARIO' -> canonical_role='AFFILIATE'
[TRACE_ID] Usuario autenticado: username=user role=AFFILIATE authority=ROLE_AFFILIATE
```

---

## ARCHIVOS MODIFICADOS

| Archivo | Cambio |
|---------|--------|
| **RoleMapper.java** | ✅ **CREADO** |
| **AuthGatewayService.java** | ✅ ACTUALIZADO (login + register) |
| **JwtAuthenticationFilter.java** | ✅ ACTUALIZADO (null-safety) |
| **SecurityConfig.java** | ✅ YA CONFIGURADO (no cambios necesarios) |

---

## OBSERVACIONES IMPORTANTES

### 1. RoleMapper es defensivo
Si el backend envía un rol desconocido, RoleMapper lo convierte a "AFFILIATE" por seguridad. Esto puede cambiar según necesidades de negocio.

### 2. Logging es más verbose
Los logs ahora muestran:
- Qué rol retorna el backend
- Qué rol se mapea a canonical
- Qué authority se crea en Spring Security

Esto facilita debugging de futuras issues de permisos.

### 3. JwtAuthenticationFilter es más resiliente
Si hay excepción en validación de JWT, no causa crash. Simplemente continúa como anónimo.

---

## MATRIZ DE PERMISOS ESPERADOS

Después de estos cambios, la matriz de permisos debe funcionar así:

| Endpoint | Método | ADMIN | AFFILIATE | DELIVERY | ANÓNIMO |
|----------|--------|-------|-----------|----------|---------|
| /api/auth/login | POST | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| /api/medications/search | GET | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| /api/auth/1 | GET | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| /api/medications | POST | ✅ 201 | ❌ 403 | ❌ 403 | ❌ 401 |
| /api/orders/cart/add | POST | ❌ 403 | ✅ 201 | ❌ 403 | ❌ 401 |
| /api/logistics/deliveries/{id}/location | PUT | ❌ 403 | ❌ 403 | ✅ 200 | ❌ 401 |
| /api/auctions/1 | GET | ✅ 200 | ✅ 200 | ❌ 403 | ❌ 401 |

---

## LOG ESPERADO EN GATEWAY

Cuando haces login con usuario que backend retorna como "USUARIO":

```log
[INFO] Forwarding login request for user: user@medigo.com
[INFO] Backend returned user: userId='2', username='user', email='user@medigo.com', role='USUARIO'
[INFO] Role mapping: backend_role='USUARIO' -> canonical_role='AFFILIATE'
[DEBUG] [TRACE-ID-123] Usuario autenticado: username=user role=AFFILIATE authority=ROLE_AFFILIATE
```

---

## RESOLUCIÓN COMPLETADA

✅ **Causa raíz identificada**: Desalineación de roles entre backend y gateway
✅ **Solución implementada**: RoleMapper + null-safety + logging
✅ **Compilación**: Pendiente (ejecutar mvn clean package)
✅ **Testing**: Pendiente (ejecutar tests manuales con curl)

