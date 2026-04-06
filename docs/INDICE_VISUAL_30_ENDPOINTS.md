# 📑 ÍNDICE VISUAL - 30 ENDPOINTS CON ROLES
## MediGo API Gateway - Mapa Completo de Acceso

---

## 📊 DISTRIBUCIÓN VISUAL

```
┌─────────────────────────────────────────────────────────────┐
│                    30 ENDPOINTS TOTALES                      │
├─────────────────────────────────────────────────────────────┤
│  🟢 8  Públicos (sin JWT)                                     │
│  🟠 8  Admin Only                                             │
│  🔵 4  Affiliate Only                                         │
│  🟣 4  Delivery Only                                          │
│  🟡 6  Admin + Affiliate (múltiples roles)                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🟢 8 ENDPOINTS PÚBLICOS (Sin JWT requerido)

| # | Endpoint | Método | Descripción |
|---|----------|--------|-------------|
| 1 | `/api/auth/login` | POST | Iniciar sesión |
| 2 | `/api/auth/register` | POST | Crear cuenta |
| 3 | `/api/medications/search?name=X` | GET | Buscar medicamentos |
| 4 | `/api/medications/branches` | GET | Ver sucursales |
| 5 | `/api/medications/branch/{id}/medications` | GET | Ver medicamentos por sucursal |
| 6 | `/api/medications/branch/{id}/stock` | GET | Ver stock de sucursal |
| 7 | `/api/medications/{id}/availability/branch/{id}` | GET | Disponibilidad en sucursal |
| 8 | `/api/medications/{id}/availability/branches` | GET | Disponibilidad en todas sucursales |

**Header requerido**: ❌ NO  
**Autenticación**: ❌ NO  
**Ejemplo curl**: 
```bash
curl http://localhost:8081/api/medications/search?name=Paracetamol
```

---

## 🟠 8 ENDPOINTS SOLO ADMIN
### (Requieren JWT con rol ADMIN)

| # | Endpoint | Método | Descripción |
|---|----------|--------|-------------|
| 9 | `/api/auth/{id}` | GET | Obtener datos de usuario |
| 10 | `/api/auth/email/{email}` | GET | Buscar usuario por email |
| 11 | `/api/medications` | POST | Crear medicamento |
| 12 | `/api/medications/{id}/branch/{id}/stock` | PUT | Actualizar stock |
| 13 | `/api/logistics/deliveries/assign` | POST | Asignar delivery |
| 14 | `/api/auctions` | POST | Crear subasta |
| 15 | `/api/auctions/{id}` | PUT | Editar subasta |

**Header requerido**: ✅ SÍ  
```
Authorization: Bearer <ADMIN_JWT_TOKEN>
Content-Type: application/json
```

**Acceso como AFFILIATE**: ❌ **403 Forbidden**  
**Acceso como DELIVERY**: ❌ **403 Forbidden**  
**Sin JWT**: ❌ **401 Unauthorized**  

**Ejemplo curl**: 
```bash
curl -X POST http://localhost:8081/api/medications \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Paracetamol 500mg",
    "unit": "tableta",
    "price": 5000
  }'
```

---

## 🔵 4 ENDPOINTS SOLO AFFILIATE (Cliente/Paciente)
### (Requieren JWT con rol AFFILIATE)

| # | Endpoint | Método | Descripción |
|---|----------|--------|-------------|
| 16 | `/api/orders/cart/add` | POST | Agregar medicamento al carrito |
| 17 | `/api/orders/cart?affiliateId={id}&branchId={id}` | GET | Ver mi carrito |
| 18 | `/api/orders` | POST | Crear orden |
| 19 | `/api/orders/{branchId}/confirm?affiliateId={id}` | POST | Confirmar orden |

**Header requerido**: ✅ SÍ  
```
Authorization: Bearer <AFFILIATE_JWT_TOKEN>
Content-Type: application/json
```

**Acceso como ADMIN**: ❌ **403 Forbidden**  
**Acceso como DELIVERY**: ❌ **403 Forbidden**  
**Sin JWT**: ❌ **401 Unauthorized**  

**Validación adicional**: Solo puede acceder a su propio carrito/órdenes (validación backend)

**Ejemplo curl**: 
```bash
curl -X POST http://localhost:8081/api/orders/cart/add \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 2,
    "branchId": 1,
    "medicationId": 5,
    "quantity": 2
  }'
```

---

## 🟣 4 ENDPOINTS SOLO DELIVERY (Repartidor)
### (Requieren JWT con rol DELIVERY)

| # | Endpoint | Método | Descripción |
|---|----------|--------|-------------|
| 20 | `/api/logistics/deliveries/active?deliveryPersonId={id}` | GET | Ver mis entregas activas |
| 21 | `/api/logistics/deliveries/{id}?deliveryPersonId={id}` | GET | Ver detalles de entrega |
| 22 | `/api/logistics/deliveries/{id}/location` | PUT | Actualizar ubicación |
| 23 | `/api/logistics/deliveries/{id}/complete` | PUT | Marcar como completada |

**Header requerido**: ✅ SÍ  
```
Authorization: Bearer <DELIVERY_JWT_TOKEN>
Content-Type: application/json
```

**Acceso como ADMIN**: ❌ **403 Forbidden**  
**Acceso como AFFILIATE**: ❌ **403 Forbidden**  
**Sin JWT**: ❌ **401 Unauthorized**  

**Validación adicional**: Solo puede ver/actualizar sus propias entregas (validación backend)

**Ejemplo curl**: 
```bash
curl -X PUT http://localhost:8081/api/logistics/deliveries/1/location \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 4.711,
    "longitude": -74.0721,
    "address": "Calle 10, Bogotá"
  }'
```

---

## 🟡 6 ENDPOINTS ADMIN + AFFILIATE (Multi-Rol)
### (Requieren JWT con rol ADMIN o AFFILIATE)

| # | Endpoint | Método | Descripción | Quién ve qué |
|---|----------|--------|-------------|---------------|
| 24 | `/api/auctions/{id}` | GET | Ver detalles subasta | Ambos ven detalles completos |
| 25 | `/api/auctions/active` | GET | Ver subastas activas | Ambos ven todas activas |
| 26 | `/api/auctions/{id}/bids` | GET | Ver ofertas de subasta | Ambos ven todas las ofertas |
| 27 | `/api/auctions/{id}/winner` | GET | Ver ganador | Ambos ven mismo ganador |
| 28 | `/api/auctions/{id}/join` | POST | Unirse a subasta | Ambos pueden unirse |
| 29 | `/api/auctions/{id}/bids` | POST | Hacer oferta | Ambos pueden ofertar |

**Header requerido**: ✅ SÍ  
```
Authorization: Bearer <ADMIN_O_AFFILIATE_JWT_TOKEN>
Content-Type: application/json
```

**Acceso como DELIVERY**: ❌ **403 Forbidden**  
**Sin JWT**: ❌ **401 Unauthorized**  

**Comportamiento especial**:
- ADMIN: Puede ver/editar TODAS las subastas
- AFFILIATE: Puede participar en subastas abiertas

**Ejemplo curl**: 
```bash
curl -X POST http://localhost:8081/api/auctions/10/bids \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "userName": "cliente1",
    "amount": 15000.0
  }'
```

---

## 🔐 ENDPOINT ESPECIAL: `/api/auth/me`

### GET `/api/auth/me?user_id={id}`

**Header requerido**: ✅ SÍ  
```
Authorization: Bearer <CUALQUIER_JWT_VALIDO>
```

**Acceso permitido**:
- ✅ ADMIN
- ✅ AFFILIATE  
- ✅ DELIVERY
- ❌ Sin JWT → **401 Unauthorized**

**Descripción**: Cada usuario puede obtener sus propios datos

**Respuesta**: Devuelve datos del usuario autenticado (NO de otro usuario)

---

## 📋 RESUMEN MATRIZ RÁPIDA

```
┌──────────┬────────┬──────────┬──────────┬──────────┬──────────┐
│ Endpoint │ Público│ Cualquiera│   ADMIN   │AFFILIATE │ DELIVERY │
│ (rol)    │(sin JWT)│(con JWT)  │ (solo)   │ (solo)   │  (solo)  │
├──────────┼────────┼──────────┼──────────┼──────────┼──────────┤
│Login     │   ✅   │           │    ✅    │    ✅    │    ✅    │
│Register  │   ✅   │           │    ✅    │    ✅    │    ✅    │
│Me        │   ❌   │    ✅     │          │          │          │
│User/{id} │   ❌   │    ❌     │    ✅    │    ❌    │    ❌    │
│Medica.   │   ✅   │           │    ✅    │    ✅    │    ✅    │
│Orders    │   ❌   │    ❌     │    ❌    │    ✅    │    ❌    │
│Logistics │   ❌   │    ❌     │    ✅    │    ❌    │    ✅    │
│Auctions  │   ❌   │    ❌     │ ✅(crear)│ ✅(join) │    ❌    │
└──────────┴────────┴──────────┴──────────┴──────────┴──────────┘
```

---

## 🎯 FLUJOS DE CASO DE USO

### 1️⃣ Cliente Nuevo - Compra Medicamento

```
ANÓNIMO          → GET /api/medications/search (sin JWT)
                 → POST /api/auth/register (crear cuenta AFFILIATE)

AFFILIATE NUEVO  → POST /api/auth/login (obtener JWT)
                 → POST /api/orders/cart/add (agregar con JWT)
                 → GET /api/orders/cart (ver con JWT)
                 → POST /api/orders (crear orden con JWT)
                 → POST /api/orders/{id}/confirm (confirmar con JWT)
```

### 2️⃣ Admin - Gestión de Medicamentos

```
ADMIN           → POST /api/auth/login (obtener JWT ADMIN)
                → POST /api/medications (crear con JWT ADMIN)
                → PUT /api/medications/{id}/branch/{id}/stock (actualizar)
                → POST /api/auctions (crear subasta)
                → GET /api/auctions/active (ver todas subastas)
```

### 3️⃣ Repartidor - Entregas

```
DELIVERY        → POST /api/auth/login (obtener JWT DELIVERY)
                → GET /api/logistics/deliveries/active (ver asignadas)
                → PUT /api/logistics/deliveries/{id}/location (actualizar ubicación)
                → PUT /api/logistics/deliveries/{id}/complete (marcar completada)
```

### 4️⃣ Subasta - Participación

```
ADMIN/AFFILIATE → POST /api/auth/login
                → GET /api/auctions/active (ver subastas abiertas)
                → POST /api/auctions/{id}/join (unirse)
                → POST /api/auctions/{id}/bids (hacer oferta)
                → GET /api/auctions/{id}/bids (ver ofertas)
                → GET /api/auctions/{id}/winner (ver ganador)
```

---

## ❌ ERRORES Y CAUSAS

### 401 Unauthorized
```
Causa: JWT falta o inválido
Solución: Agregar header Authorization: Bearer <token>
Afecta: Todos endpoints protegidos
```

### 403 Forbidden
```
❌ Causa: JWT válido pero rol incorrecto
   Ej: AFFILIATE intentando crear medicamento

Solución:
1. Verificar tu rol en el JWT
2. Usar endpoint permitido para tu rol
3. Si necesitas más permisos, contactar admin

Códigos de rol:
- ADMIN → Puede hacer todo
- AFFILIATE → Órdenes y subastas (participar)
- DELIVERY → Solo logistics propio
```

### 400 Bad Request
```
Causa: Parámetros inválidos o faltantes
Solución: Revisar body y query params del endpoint
```

### 404 Not Found
```
Causa: Recurso no existe
Solución: Verificar IDs en la URL
```

### 500 Internal Server Error
```
Causa: Error del servidor o validación fallida
Solución: Revisar logs y contactar admin
```

---

## 🔗 REFERENCIAS CRUZADAS

**Documentación detallada**: 
- [ENDPOINTS_POR_ROL_DETALLADO.md](ENDPOINTS_POR_ROL_DETALLADO.md) - Todos 30 con request/response
- [GUIA_RAPIDA_ROLES.md](GUIA_RAPIDA_ROLES.md) - Resumen comprimido
- [API_GATEWAY_ENDPOINTS_Y_VALIDACIONES.md](API_GATEWAY_ENDPOINTS_Y_VALIDACIONES.md) - Especificación técnica

---

## ✅ CHECKLIST - VALIDACIÓN DE ACCESO

Al hacer una llamada a endpoint, validar:

```
□ ¿Es endpoint público? 
  ✅ Sí → No necesitas JWT
  ❌ No → Continuar...

□ ¿Tengo JWT válido?
  ✅ Sí → Incluir en header Authorization: Bearer <token>
  ❌ No → Hacer login primero

□ ¿Mi rol es el permitido?
  ✅ Sí → Hacer la llamada
  ❌ No → Usar endpoint adecuado para mi rol

□ ¿Me dan 403?
  → Tu rol no tiene permiso
  → Verifica que usas el endpoint correcto
  → Contacta admin si necesitas permisos adicionales
```

---

**Última actualización**: 2026-04-03  
**Completitud**: 100% - 30/30 endpoints analizados  
**Status**: Active y validado

