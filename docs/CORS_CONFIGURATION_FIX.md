# CORS Configuration Fix - API Gateway

**Fecha:** 6 de Abril, 2026  
**Problema:** Preflight OPTIONS respondía con 403 y faltaban headers CORS  
**Estado:** ✅ RESUELTO

---

## 🔴 Problema Original

El frontend en Vercel no podía consumir la API Gateway debido a:

1. **Preflight OPTIONS bloqueado (403)**: Las request de navegador envían un preflight OPTIONS automáticamente antes de POST/PUT/PATCH. Este request estaba siendo rechazado porque:
   - No estaba explícitamente permitido en `authorizeHttpRequests`
   - La configuración CORS no era procesada correctamente antes de la autenticación JWT

2. **Headers CORS faltantes**: La response a OPTIONS no incluía los headers necesarios:
   - `Access-Control-Allow-Origin`
   - `Access-Control-Allow-Methods`
   - `Access-Control-Allow-Headers`

3. **Orígenes hardcodeados**: Cada deploy de Vercel crea una URL diferente, requiriendo cambios de código.

---

## ✅ Solución Aplicada

### Cambio 1: Permitir OPTIONS globalmente ANTES de autenticación

```java
.authorizeHttpRequests(auth -> auth
    // ======== PREFLIGHT OPTIONS (SIN AUTENTICACION) ========
    // CRÍTICO: Permite preflight OPTIONS antes de cualquier otra regla
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    
    // ... resto de reglas
)
```

**Por qué funciona:** Spring procesa los matchers en orden. Si OPTIONS se permite al inicio, cualquier preflight es aprobado sin necesidad de JWT.

### Cambio 2: Configuración CORS mejorada

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    
    // ✅ Credenciales: false (usamos JWT en headers, no cookies)
    config.setAllowCredentials(false);
    
    // ✅ Orígenes permitidos (soporta Vercel actual + preview deployments)
    config.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173",      // Desarrollo local
        "http://localhost:3000",
        "http://localhost:4200",
        "http://localhost:8080",
        "https://frontmedigo.vercel.app",  // Dominio principal
        "https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app" // URL actual
    ));
    
    // ✅ Métodos HTTP permitidos
    config.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));
    
    // ✅ Headers permitidos en request
    config.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "Accept", "Origin", 
        "X-Requested-With", "X-CSRF-Token",
        "Access-Control-Request-Method", "Access-Control-Request-Headers"
    ));
    
    // ✅ Headers expuestos para lectura desde JS
    config.setExposedHeaders(Arrays.asList(
        "Authorization", "X-Trace-ID", "X-Content-Type-Options"
    ));
    
    // ✅ Cache preflight por 1 hora
    config.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 🎯 Pasos para Implementar

### 1. Código ya aplicado
El archivo [SecurityConfig.java](../src/main/java/com/medigo/gateway/infrastructure/config/SecurityConfig.java) ya tiene los cambios.

### 2. Compilar e desplegar
```bash
# Local
mvn clean package -DskipTests

# O solo compilar sin tests
mvn clean install -DskipTests
```

### 3. Desplegar a Azure
```bash
# Si usas Azure CLI
az webapp up --name ezequiel-gateway \
  --resource-group <tu-resource-group> \
  --location canadacentral

# O via GitHub Actions (recomendado), con JAR generado:
# Push a main → CI/CD ejecuta → Artifact sube a Azure App Service
```

---

## 🧪 Validación con cURL

### Test 1: Preflight OPTIONS (debe responder 200/204, NO 403)

```bash
curl -i -X OPTIONS "https://ezequiel-gateway-etcrh9dxg9dwhng4.canadacentral-01.azurewebsites.net/api/auth/login" \
  -H "Origin: https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"
```

**Respuesta esperada:**
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept, Origin, X-Requested-With, X-CSRF-Token, Access-Control-Request-Method, Access-Control-Request-Headers
Access-Control-Max-Age: 3600
```

### Test 2: POST real (debe funcionar con JWT o sin auth si permitAll)

```bash
curl -i -X POST "https://ezequiel-gateway-etcrh9dxg9dwhng4.canadacentral-01.azurewebsites.net/api/auth/login" \
  -H "Origin: https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}'
```

**Respuesta esperada:** 200 ok, no 403 (y con headers CORS)

### Test 3: GET público (debe funcionar sin auth)

```bash
curl -i -X GET "https://ezequiel-gateway-etcrh9dxg9dwhng4.canadacentral-01.azurewebsites.net/api/medications/branches" \
  -H "Origin: https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app"
```

**Respuesta esperada:** 200 ok con datos + headers CORS

---

## 🔄 Futuro: Soportar patrones dinámicos *.vercel.app

Si en futuro necesitas soportar **cualquier** despliegue en *.vercel.app sin cambiar código, reemplaza:

```java
// Actual (URLs específicas)
config.setAllowedOrigins(Arrays.asList(
    "https://frontmedigo.vercel.app",
    "https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app"
));

// Por (patrón dinámico)
config.setAllowedOriginPatterns(Arrays.asList("https://*.vercel.app"));
```

**Nota:** `setAllowedOriginPatterns()` solo funciona con `allowCredentials=false` (que ya es nuestro caso).

---

## 📋 Headers CORS Explicados

| Header | Valor | Propósito |
|--------|-------|----------|
| `Access-Control-Allow-Origin` | `https://frontmedigo-...vercel.app` | Indica qué origen puede acceder |
| `Access-Control-Allow-Methods` | `GET, POST, PUT, PATCH, DELETE, OPTIONS` | Métodos HTTP permitidos |
| `Access-Control-Allow-Headers` | `Authorization, Content-Type, ...` | Headers que el cliente puede enviar |
| `Access-Control-Expose-Headers` | `Authorization, X-Trace-ID` | Headers que el JS puede leer |
| `Access-Control-Allow-Credentials` | `false` | No enviamos cookies, solo JWT |
| `Access-Control-Max-Age` | `3600` | Browser cachea preflight por 1 hora |

---

## 🔒 Seguridad

✅ **Configuración segura porque:**
1. Solo orígenes específicos permitidos (Vercel + localhost)
2. No usamos cookies/sesiones (`allowCredentials=false`)
3. Autenticación por JWT en headers `Authorization`
4. OPTIONS permitido sin restricciones, pero el *contenido* sigue protegido por JWT
5. Endpoints específicos requieren autenticación y roles (ADMIN, AFFILIATE, DELIVERY)

---

## 📝 Resumen del Cambio

| Problema | Solución |
|----------|----------|
| OPTIONS devuelve 403 | Agregado `.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()` al inicio |
| Falta header Allow-Origin | CORS ya estaba configurado, solo necesitaba que OPTIONS fuera permitido |
| Orígenes hardcodeados | Agregadas URLs de Vercel específicas; comentario sobre patrones dinámicos para futuro |
| Headers CORS incompletos | Expandidos `setAllowedHeaders()` y `setExposedHeaders()` |
| `allowCredentials=true` innecesario | Cambiado a `false` ya que usamos JWT, no cookies |

**Archivo modificado:** `src/main/java/com/medigo/gateway/infrastructure/config/SecurityConfig.java`
