# Arquitectura del Gateway MediGo

## Hexagonal (Ports & Adapters)

```
[Cliente HTTP/WS]
       |
[Infrastructure - in]  ← Controllers (Adapter in)
       |
[Application]          ← Services (Use Cases)
       |
[Domain]               ← Ports (interfaces) + Models
       |
[Infrastructure - out] ← RestTemplate, Redis, JWT (Adapters out)
       |
[Backend MediGo :8080]
```

## Flujo JWT

```
POST /api/auth/login
  → AuthController
  → AuthGatewayService
  → BackendClient → backend :8080/api/auth/login
  ← {id, username, role}
  → JjwtAdapter.generateToken()
  ← {jwtToken: "eyJ..."}
```

## Circuit Breaker

```
RestTemplateBackendClient
  @CircuitBreaker(backendCB)
    OK  → ResponseEntity
    5 fallos / 10s → OPEN
    OPEN → fallback() → 503
    10s → HALF_OPEN → prueba
```
