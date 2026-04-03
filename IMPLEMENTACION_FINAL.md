# 🚀 APIGATEWAY MEDIGO - DOCUMENTACIÓN FINAL

**Versión**: 1.0  
**Fecha**: 2026-04-02  
**Status**: ✅ IMPLEMENTACIÓN COMPLETADA  

---

## 📋 TABLA DE CONTENIDOS

1. [Descripción General](#descripción-general)
2. [Stack Tecnológico](#stack-tecnológico)
3. [Configuración](#configuración)
4. [Arquitectura](#arquitectura)
5. [Endpoints Expuestos](#endpoints-expuestos)
6. [Autenticación JWT](#autenticación-jwt)
7. [Rate Limiting](#rate-limiting)
8. [Auditoría](#auditoría)
9. [Circuit Breaker](#circuit-breaker)
10. [Guía de Uso](#guía-de-uso)
11. [Deploy](#deploy)

---

## 🎯 Descripción General

El **APIGATEWAY MEDIGO** es un **proxy inteligente** que:

- ✅ Redirige TODAS las peticiones `/api/*` al Backend MediGo (:8080)
- ✅ Valida **JWT** en cada petición (excepto login/register)
- ✅ Aplica **rate limiting** centralizado (Redis)
- ✅ **Audita** todas las peticiones en BD (PostgreSQL)
- ✅ Maneja **CORS** automáticamente
- ✅ Implementa **circuit breaker** (Resilience4j)
- ✅ **Propaga Trace ID** para trazabilidad distribuida

**Objetivo**: Centralizar seguridad, auditoría y control sin modificar la lógica del Backend.

---

## 🛠️ Stack Tecnológico

| Componente | Técnologia | Versión |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.1.5 |
| **Java** | JDK | 21 |
| **Puerto** | - | **8081** |
| **Seguridad** | Spring Security | 6 |
| **Autenticación** | JWT (JJWT) | 0.12.3 |
| **ORM** | JPA/Hibernate | Incluido |
| **BD** | PostgreSQL | 12+ |
| **Cache/Rate Limit** | Redis | (Upstash en producción) |
| **Resilencia** | Resilience4j | 2.0.2 |
| **Tracing** | Custom TraceID | - |
| **API Docs** | Swagger/OpenAPI 3.0 | 2.2.0 |
| **Build** | Maven | 3.9+ |

---

## ⚙️ Configuración

### Archivo: `application.properties`

```properties
# SERVER
server.port=8081

# SPRING
spring.application.name=medigo-api-gateway

# DATABASE (PostgreSQL - Auditoría del Gateway)
spring.datasource.url=jdbc:postgresql://localhost:5432/medigo
spring.datasource.username=postgres
spring.datasource.password=medigo_secure_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/HIBERNATE
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# REDIS (Cache, Rate Limiting, Token Blacklist)
spring.data.redis.url=redis://localhost:6379
spring.data.redis.timeout=2000ms

# GATEWAY
gateway.backend.base-url=http://localhost:8080
gateway.backend.timeout-seconds=30

# JWT
gateway.jwt.secret=medigo-super-secret-key-must-be-at-least-256-bits-long
gateway.jwt.expiration-ms=86400000

# RATE LIMITING
gateway.rate-limit.global-per-minute=1000
gateway.rate-limit.user-per-minute=100
gateway.rate-limit.bid-per-minute=10

# ACTUATOR (Métricas)
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=always

# RESILIENCE4J - CIRCUIT BREAKER
resilience4j.circuitbreaker.instances.backendCB.registerHealthIndicator=true
resilience4j.circuitbreaker.instances.backendCB.slidingWindowSize=10
resilience4j.circuitbreaker.instances.backendCB.failureRateThreshold=50
resilience4j.circuitbreaker.instances.backendCB.waitDurationInOpenState=30s
resilience4j.circuitbreaker.instances.backendCB.permittedNumberOfCallsInHalfOpenState=3

# RETRY
resilience4j.retry.instances.backendRetry.maxAttempts=3
resilience4j.retry.instances.backendRetry.waitDuration=500ms

# LOGGING
logging.level.com.medigo.gateway=DEBUG
logging.level.org.springframework.security=INFO
```

### Variables de Entorno

Para **producción**, usar variables de entorno en lugar de valores hardcodeados:

```bash
export DB_URL=jdbc:postgresql://<host>:5432/medigo
export DB_USER=<username>
export DB_PASSWORD=<password>
export REDIS_URL=rediss://<host>:6379
export BACKEND_URL=https://backend.medigo.com
export JWT_SECRET=<clave-secreta-256-bits-minimo>
export JWT_EXPIRATION_MS=86400000
```

---

## 🏗️ Arquitectura

### Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────────┐
│              ADAPTADORES DE ENTRADA                 │
│        (Controllers REST - Reciben peticiones)      │
├──────────────────┬──────────────────┬───────────────┤
│ AuthController   │ CatalogController│ OrderController
│ :8081           │ :8081            │ :8081
└──────────────────┴──────────────────┴───────────────┘
         ↓                  ↓                  ↓
┌─────────────────────────────────────────────────────┐
│            INTERCEPTORES/FILTROS                    │
│  JwtAuthenticationFilter (validar JWT)              │
│  RateLimitInterceptor (throttling)                  │
│  AuditLoggingInterceptor (auditoría)                │
└─────────────────────────────────────────────────────┘
         ↓                  ↓                  ↓
┌─────────────────────────────────────────────────────┐
│             SERVICIOS DE APLICACIÓN                 │
│  ForwardingService (proxy al backend)               │
│  AuditingService (registrar logs)                   │
│  AuthGatewayService (JWT generation)                │
└─────────────────────────────────────────────────────┘
         ↓                  ↓                  ↓
┌─────────────────────────────────────────────────────┐
│        ADAPTADORES DE SALIDA (Puertos)              │
│  RestTemplateBackendClient (HTTP al Backend)        │
│  RedisRateLimitAdapter (Control de frecuencia)      │
│  JpaAuditLogAdapter (Persistencia de logs)          │
└─────────────────────────────────────────────────────┘
         ↓                  ↓                  ↓
┌─────────────────────────────────────────────────────┐
│    RECURSOS EXTERNOS (Backend, BD, Cache)           │
│  Backend :8080 (PostgreSQL/Supabase)                │
│  PostgreSQL (medigo.audit_logs)                     │
│  Redis (Rate limiting + Cache)                      │
└─────────────────────────────────────────────────────┘
```

---

## 📡 Endpoints Expuestos

El Gateway expone **exactamente los mismos endpoints que el Backend**. La diferencia es que agrega validación, control y auditoría.

### 🔓 PÚBLICOS (sin JWT)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Autenticación con credentials |
| POST | `/api/auth/register` | Registro de nuevo usuario |
| GET | `/api/medications/search?name=X` | Buscar medicamentos |
| GET | `/api/medications/branch/{branchId}/stock` | Stock en sucursal |
| GET | `/api/medications/branch/{branchId}/medications` | Medicamentos de sucursal |
| GET | `/api/medications/branches` | Listar sucursales |

### 🔐 PROTEGIDOS (JWT requerido)

#### Autenticación
| Método | Endpoint | Rol |
|--------|----------|-----|
| GET | `/api/auth/me` | CLIENTE, REPARTIDOR, ADMIN |
| GET | `/api/auth/{id}` | CLIENTE, REPARTIDOR, ADMIN |
| GET | `/api/auth/email/{email}` | CLIENTE, REPARTIDOR, ADMIN |

#### Catálogo
| Método | Endpoint | Rol |
|--------|----------|-----|
| GET | `/api/catalog/medications` | Todos |
| GET | `/api/catalog/medications/{id}` | Todos |
| POST | `/api/catalog/medications` | **ADMIN** |
| PUT | `/api/catalog/medications/{id}` | **ADMIN** |
| PUT | `/api/catalog/medications/{id}/stock` | **ADMIN** |

#### Órdenes
| Método | Endpoint | Rol |
|--------|----------|-----|
| POST | `/api/orders/cart/add` | CLIENTE |
| GET | `/api/orders/cart` | CLIENTE |
| DELETE | `/api/orders/cart/{cartId}/{medicationId}` | CLIENTE |
| POST | `/api/orders/confirm` | CLIENTE |
| GET | `/api/orders/{orderId}` | CLIENTE |
| GET | `/api/orders/affiliate/{affiliateId}` | CLIENTE |

#### Logística
| Método | Endpoint | Rol |
|--------|----------|-----|
| GET | `/api/logistics/deliveries/active` | **REPARTIDOR** |
| GET | `/api/logistics/deliveries/{id}` | **REPARTIDOR** |
| PUT | `/api/logistics/deliveries/{id}/complete` | **REPARTIDOR** |
| PUT | `/api/logistics/deliveries/{id}/location` | **REPARTIDOR** |
| GET | `/api/logistics/deliveries/{id}/location` | **REPARTIDOR** |

#### Subastas
| Método | Endpoint | Rol |
|--------|----------|-----|
| GET | `/api/auctions/active` | Todos |
| GET | `/api/auctions/{id}` | Todos |
| GET | `/api/auctions/{id}/bids` | Todos |
| GET | `/api/auctions/{id}/winner` | Todos |
| POST | `/api/auctions` | **ADMIN** |
| PUT | `/api/auctions/{id}` | **ADMIN** |
| POST | `/api/auctions/{id}/join` | CLIENTE |
| POST | `/api/auctions/{id}/bids` | CLIENTE |

---

## 🔐 Autenticación JWT

### Flujo de Autenticación

```
1. Cliente envía: POST /api/auth/login
   Body: { "email": "user@test.com", "password": "pass123" }
   ↓
2. Gateway (ForwardingService) → Backend :8080
   ↓
3. Backend valida credenciales y retorna:
   { "id": 123, "username": "user", "role": "CLIENTE" }
   ↓
4. Gateway (JwtAdapter) genera JWT:
   { "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5..." }
   ↓
5. Cliente usa JWT en peticiones futuras:
   Authorization: Bearer <JWT>
```

### Validación JWT en Peticiones

```
Petición: GET /api/auth/me
Header: Authorization: Bearer eyJ...
↓
Gateway (JwtAuthenticationFilter):
  1. Extrae token del header
  2. Valida firma con gateway.jwt.secret
  3. Valida expiración (86400000 ms = 24 horas)
  4. Extrae: userId, username, role
  5. Propaga al SecurityContext
  ↓
¿JWT válido?
  SÍ → Continúa a siguiente filtro
  NO → Retorna 401 Unauthorized
```

### Claims del JWT

```json
{
  "sub": "123",                    // userId
  "username": "user@test.com",     // email
  "role": "CLIENTE",               // CLIENTE, REPARTIDOR, ADMIN
  "iat": 1712125600,              // issued at (timestamp)
  "exp": 1712212000               // expiration (timestamp + 86400)
}
```

---

## ⏱️ Rate Limiting

### Límites Configurados

| Tipo | Límite | Ventana |
|------|--------|---------|
| **Global** | 1000 req/minuto | 60 segundos |
| **Por Usuario** | 100 req/minuto | 60 segundos |
| **Por Pujas** | 10 req/minuto | 60 segundos |

### Almacenamiento en Redis

```
Contador Global:
  Key: "global:requests:{minuto}"
  Value: count
  TTL: 60 segundos

Contador por Usuario:
  Key: "user:{userId}:requests:{minuto}"
  Value: count
  TTL: 60 segundos

Contador de Pujas:
  Key: "user:{userId}:bids:{minuto}"
  Value: count
  TTL: 60 segundos
```

### Respuesta de Rate Limit Excedido

```
Status: 429 Too Many Requests
Headers:
  - X-RateLimit-Limit: 100
  - X-RateLimit-Remaining: 0
  - X-RateLimit-Reset: <epoch-timestamp>
  - Retry-After: 60

Body:
{
  "error": "Rate limit exceeded",
  "message": "Máximo de peticiones por minuto alcanzado"
}
```

---

## 📊 Auditoría

### Tabla `audit_logs`

```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,                          -- NULL si público
    client_ip VARCHAR(45),                   -- IPv4/IPv6
    method VARCHAR(10),                      -- GET, POST, PUT, DELETE
    endpoint VARCHAR(255),                   -- /api/orders/confirm
    query_params TEXT,                       -- ?page=1&limit=10
    request_body TEXT,                       -- JSON del request
    status_code INT,                         -- 200, 401, 404, 500
    response_body TEXT,                      -- JSON de error (opcional)
    duration_ms BIGINT,                      -- Tiempo de respuesta
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trace_id VARCHAR(100),                   -- Para tracing distribuido
    
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at),
    KEY idx_endpoint (endpoint),
    KEY idx_status_code (status_code)
);
```

### Ejemplo de Log

```json
{
  "id": 1,
  "userId": 123,
  "clientIp": "192.168.1.100",
  "method": "POST",
  "endpoint": "/api/orders/confirm",
  "statusCode": 200,
  "durationMs": 245,
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-04-02T15:30:45"
}
```

### Consultas de Auditoría

**Últimos 100 logs**:
```java
List<AuditLog> latest = auditLogRepository.findLatest(100);
```

**Logs de un usuario**:
```java
List<AuditLog> userLogs = auditLogRepository.findByUserIdAndDateRange(
    userId, 
    LocalDateTime.now().minusDays(1),
    LocalDateTime.now()
);
```

**Logs por endpoint**:
```java
List<AuditLog> endpointLogs = auditLogRepository.findByEndpoint("/api/orders/confirm");
```

**Logs por status (errores)**:
```java
List<AuditLog> errors = auditLogRepository.findByStatusCode(500);
```

---

## 🔌 Circuit Breaker

### Configuración Resilience4j

```properties
# Cuando Backend falla consecutivamente
failureRateThreshold=50%           # 50% de errores
slidingWindowSize=10               # Analiza últimas 10 llamadas

# Estado OPEN (circuito abierto)
waitDurationInOpenState=30s        # Espera 30s antes de intentar cerrar

# Estado HALF_OPEN (pruebas)
permittedNumberOfCallsInHalfOpenState=3  # Intenta 3 veces
```

### Estados del Circuito

```
CLOSED (Normal)
  ↓ (5 errores consecutivos)
OPEN (Rechaza peticiones)
  ↓ (después de 30s)
HALF_OPEN (Intenta reconectar)
  ├─ SÍ (éxito) → CLOSED
  └─ NO (falla) → OPEN
```

### Respuesta si Backend Está Down

```
Status: 503 Service Unavailable
Headers:
  - Retry-After: 30

Body:
{
  "error": "Backend no disponible temporalmente",
  "message": "El servicio está experimentando problemas. Intente nuevamente en 30 segundos."
}
```

---

## 🚀 Guía de Uso

### Startup Local

```bash
# 1. Iniciar PostgreSQL (si no está corriendo)
docker run -d \
  --name postgres-medigo \
  -e POSTGRES_DB=medigo \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# 2. Iniciar Redis (si no está corriendo)
docker run -d \
  --name redis-medigo \
  -p 6379:6379 \
  redis:7-alpine

# 3. Iniciar Backend MediGo
cd ../backend
mvn spring-boot:run

# 4. Iniciar Gateway
mvn spring-boot:run

# Gateway disponible en: http://localhost:8081
```

### Test de Endpoints

```bash
# 1. Login (obtener JWT)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"pass123"}'

# Respuesta:
{
  "success": true,
  "data": {
    "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "traceId": "550e8400-e29b-41d4-a716-446655440000"
}

# 2. Usar JWT en petición protegida
JWT="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer $JWT"

# 3. Buscar medicamentos (público, sin JWT)
curl -X GET "http://localhost:8081/api/medications/search?name=para"

# 4. Confirmar orden (protegido)
curl -X POST http://localhost:8081/api/orders/confirm \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT" \
  -d '{"cartId":1}'

# 5. Ver logs de auditoría (desde BD)
psql -U postgres -d medigo -c "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 10;"
```

### Swagger/OpenAPI

Acceder a: http://localhost:8081/swagger-ui.html

O descargar OpenAPI JSON: http://localhost:8081/api-docs

---

## 🚢 Deploy

### Producción (AWS/Azure/DigitalOcean)

#### 1. Preparar aplicación

```bash
# Build JAR
mvn clean package -DskipTests

# JAR en: target/medigo-api-gateway-1.0.0.jar
```

#### 2. Variables de entorno (en servidor)

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://prod-db.example.com:5432/medigo
export DB_USER=medigo_user
export DB_PASSWORD=<secure-password>
export REDIS_URL=rediss://prod-cache.upstash.io:6379
export BACKEND_URL=https://api.medigo.com
export JWT_SECRET=<256-bit-secret-key>
```

#### 3. Docker (Producción)

```dockerfile
FROM openjdk:21-slim
COPY target/medigo-api-gateway-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8081
```

```bash
docker build -t medigo-gateway:1.0.0 .
docker run -d \
  --name medigo-gateway \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://db:5432/medigo \
  -e REDIS_URL=rediss://cache:6379 \
  medigo-gateway:1.0.0
```

#### 4. Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: medigo-gateway
spec:
  replicas: 3
  selector:
    matchLabels:
      app: medigo-gateway
  template:
    metadata:
      labels:
        app: medigo-gateway
    spec:
      containers:
      - name: medigo-gateway
        image: medigo-gateway:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: medigo-secrets
              key: db_url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /api/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
```

---

## 📊 Monitoreo

### Endpoints de Salud

```bash
# Health check
curl http://localhost:8081/actuator/health

# Métricas Prometheus
curl http://localhost:8081/actuator/prometheus

# Info de aplicación
curl http://localhost:8081/actuator/info
```

### Logs Importantes

Nivel DEBUG log:
```
[TRACE-ID] Forwarding POST /api/orders/confirm
[TRACE-ID] Rate limit: 45/100 requests remaining
[TRACE-ID] Usuario autenticado: user123 rol: CLIENTE
[TRACE-ID] Audit: POST /api/orders/confirm - Status: 200 - Duration: 245ms
[TRACE-ID] Circuit breaker OPEN - Backend unavailable
```

---

## 🎓 Resumen

El APIGATEWAY MEDIGO es un proxy que:

1. ✅ **Valida JWT** en peticiones protegidas
2. ✅ **Controla frecuencia** vía Redis rate limiting
3. ✅ **Audita** todas las peticiones en PostgreSQL
4. ✅ **Forwarda transparentemente** al Backend
5. ✅ **Recupera automáticamente** si Backend falla (Circuit Breaker)
6. ✅ **Propaga Trace ID** para debugging distribuido

**Flujo completo de una petición**:

```
Cliente :9000
   ↓
GET /api/auth/me + JWT
   ↓
Gateway :8081
   ├─ JwtAuthenticationFilter (valida JWT)
   ├─ RateLimitInterceptor (verifica límite)
   ├─ ForwardingService (envía al Backend)
   └─ AuditLoggingInterceptor (registra en BD)
   ↓
Backend :8080
   ├─ AuthController.getMe()
   └─ Retorna { "id": 123, "username": "user" }
   ↓
Gateway :8081
   ├─ AuditLog guardado en BD
   └─ Respuesta retornada a Cliente
   ↓
Cliente recibe respuesta idéntica
```

---

## 📞 Soporte

- Documentación Backend: `/docs` del proyecto Backend MediGo
- Swagger UI: http://localhost:8081/swagger-ui.html
- Logs Auditoría: `SELECT * FROM audit_logs WHERE created_at > NOW() - INTERVAL '1 hour'`
- Troubleshooting: Ver archivo `ARCHITECTURE.md`

---

**¡Gateway listo para producción!** 🚀
