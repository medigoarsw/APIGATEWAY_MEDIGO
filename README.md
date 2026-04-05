# MediGo API Gateway

Reverse proxy **seguro, resiliente e inteligente** para la plataforma MediGo.
Puerto: `8081` | Backend: `8080`

## Stack
- Java 21 · Spring Boot 3.1.5
- JJWT 0.12.3 · Resilience4j · Redis (Upstash)
- PostgreSQL / Supabase (indirecto vía backend)

## Setup Local

```bash
git clone <repo>
cd medigo-api-gateway

# Variables de entorno (o editar application.yml)
export BACKEND_URL=http://localhost:8080
export JWT_SECRET=medigo-super-secret-key-must-be-at-least-256-bits-long
export REDIS_HOST=localhost
export REDIS_PORT=6379

mvn clean install
mvn spring-boot:run
```

## URLs

| Recurso       | URL                                      |
|---------------|------------------------------------------|
| Swagger UI    | http://localhost:8081/swagger-ui.html    |
| OpenAPI JSON  | http://localhost:8081/api-docs           |
| Health        | http://localhost:8081/api/health         |
| Prometheus    | http://localhost:8081/actuator/prometheus|

## Endpoints principales

| Método | Ruta                          | Rol requerido         |
|--------|-------------------------------|------------------------|
| POST   | /api/auth/login               | Público               |
| GET    | /api/auctions                 | ADMIN, USUARIO        |
| POST   | /api/auctions                 | ADMIN                 |
| POST   | /api/auctions/{id}/bids       | ADMIN, USUARIO        |
| GET    | /api/catalog/medications      | ADMIN                 |
| POST   | /api/orders                   | ADMIN, USUARIO        |
| GET    | /api/logistics/deliveries/{id}| ADMIN, REPARTIDOR     |

## WebSocket

```
ws://localhost:8081/ws
Topics: /topic/auction/{id} | /topic/logistics/locations
Auth: ?token=<jwt>
```

## Tests

```bash
mvn test                    # Unit + Integration
mvn jacoco:report           # Cobertura en target/site/jacoco
```

## Variables de entorno

| Variable         | Descripción               | Default                        |
|------------------|---------------------------|--------------------------------|
| BACKEND_URL      | URL del backend MediGo    | http://localhost:8080          |
| JWT_SECRET       | Secret JWT (≥256 bits)    | —                              |
| JWT_EXPIRATION_MS| Expiración JWT en ms      | 86400000 (24h)                 |
| REDIS_HOST       | Host Redis (Upstash)      | localhost                      |
| REDIS_PORT       | Puerto Redis              | 6379                           |
| REDIS_PASSWORD   | Password Redis            | —                              |
