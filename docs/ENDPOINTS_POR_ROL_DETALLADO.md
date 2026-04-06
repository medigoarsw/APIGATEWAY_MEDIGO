# 📋 API Gateway - Documentación Completa de Endpoints por Rol
## MediGo Backend - 30 Endpoints Analizados
**Fecha**: 2026-04-03 | **Status**: Documentación Completa con Roles

---

## 📊 TABLA RESUMEN - QUIÉN PUEDE USAR CADA ENDPOINT

| # | Endpoint | Método | **👥 ROLES PERMITIDOS** | Pub. | Auth | Admin | Aff. | Deliv. |
|---|----------|--------|------------------------|-----|------|-------|------|--------|
| **AUTH - 5 endpoints** |
| 1 | `/api/auth/login` | POST | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 2 | `/api/auth/register` | POST | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 3 | `/api/auth/me` | GET | **Cualquiera autenticado** | ❌ | ✅ | ✅ | ✅ | ✅ |
| 4 | `/api/auth/{id}` | GET | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| 5 | `/api/auth/email/{email}` | GET | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| **CATALOG - 8 endpoints** |
| 6 | `/api/medications/search` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 7 | `/api/medications/branch/{branchId}/stock` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 8 | `/api/medications/branch/{branchId}/medications` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 9 | `/api/medications/branches` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 10 | `/api/medications/{id}/availability/branch/{branchId}` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 11 | `/api/medications/{id}/availability/branches` | GET | **Todos (Público)** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 12 | `/api/medications` | POST | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| 13 | `/api/medications/{id}/branch/{id}/stock` | PUT | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| **ORDERS - 4 endpoints** |
| 14 | `/api/orders/cart/add` | POST | **Solo AFFILIATE** | ❌ | ❌ | ❌ | ✅ | ❌ |
| 15 | `/api/orders/cart` | GET | **Solo AFFILIATE** | ❌ | ❌ | ❌ | ✅ | ❌ |
| 16 | `/api/orders` | POST | **Solo AFFILIATE** | ❌ | ❌ | ❌ | ✅ | ❌ |
| 17 | `/api/orders/{branchId}/confirm` | POST | **Solo AFFILIATE** | ❌ | ❌ | ❌ | ✅ | ❌ |
| **LOGISTICS - 5 endpoints** |
| 18 | `/api/logistics/deliveries/{id}/location` | PUT | **Solo DELIVERY** | ❌ | ❌ | ❌ | ❌ | ✅ |
| 19 | `/api/logistics/deliveries/{id}/complete` | PUT | **Solo DELIVERY** | ❌ | ❌ | ❌ | ❌ | ✅ |
| 20 | `/api/logistics/deliveries/active` | GET | **Solo DELIVERY** | ❌ | ❌ | ❌ | ❌ | ✅ |
| 21 | `/api/logistics/deliveries/{id}` | GET | **Solo DELIVERY** | ❌ | ❌ | ❌ | ❌ | ✅ |
| 22 | `/api/logistics/deliveries/assign` | POST | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| **AUCTIONS - 8 endpoints** |
| 23 | `/api/auctions` | POST | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| 24 | `/api/auctions/{id}` | PUT | **Solo ADMIN** | ❌ | ❌ | ✅ | ❌ | ❌ |
| 25 | `/api/auctions/{id}` | GET | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |
| 26 | `/api/auctions/active` | GET | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |
| 27 | `/api/auctions/{id}/bids` | GET | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |
| 28 | `/api/auctions/{id}/winner` | GET | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |
| 29 | `/api/auctions/{id}/join` | POST | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |
| 30 | `/api/auctions/{id}/bids` | POST | **ADMIN + AFFILIATE** | ❌ | ❌ | ✅ | ✅ | ❌ |

**Leyenda**:
- **Pub.** = Público (sin autenticación)
- **Auth** = Autenticado (cualquier usuario con JWT válido)
- **Admin** = Solo administrador (rol ADMIN)
- **Aff.** = Solo afiliado (rol AFFILIATE - cliente/paciente)
- **Deliv.** = Solo repartidor (rol DELIVERY)

---

## 🔐 DISTRIBUCIÓN POR ROL

### 👑 ADMIN (Administrador)
**16 endpoints en total**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/auth/{id}` | GET | Obtener datos de cualquier usuario |
| `/api/auth/email/{email}` | GET | Obtener usuario por email |
| `/api/medications` | POST | Crear nuevo medicamento |
| `/api/medications/{id}/branch/{id}/stock` | PUT | Actualizar stock de medicamento |
| `/api/logistics/deliveries/assign` | POST | Asignar deliveries |
| `/api/auctions` | POST | Crear subasta |
| `/api/auctions/{id}` | PUT | Editar subasta |
| `/api/auctions/{id}` | GET | Ver detalles de subasta |
| `/api/auctions/active` | GET | Ver subastas activas |
| `/api/auctions/{id}/bids` | GET | Ver ofertas de subasta |
| `/api/auctions/{id}/winner` | GET | Ver ganador de subasta |
| `/api/auctions/{id}/join` | POST | Unirse a subasta |
| `/api/auctions/{id}/bids` | POST | Hacer oferta en subasta |
| `/api/auth/login` | POST | Login (todos) |
| `/api/auth/register` | POST | Registro (todos) |
| `/api/auth/me` | GET | Obtener mi perfil |

**Acceso a**:
- Gestión de usuarios
- Gestión de medicamentos
- Gestión de deliveries
- Gestión de subastas
- Visualización de catálogo público

---

### 👤 AFFILIATE (Cliente/Paciente)
**10 endpoints en total**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/orders/cart/add` | POST | Agregar medicamento al carrito |
| `/api/orders/cart` | GET | Ver mi carrito |
| `/api/orders` | POST | Crear orden |
| `/api/orders/{branchId}/confirm` | POST | Confirmar orden |
| `/api/auctions/{id}` | GET | Ver detalles de subasta |
| `/api/auctions/active` | GET | Ver subastas activas |
| `/api/auctions/{id}/bids` | GET | Ver ofertas de subasta |
| `/api/auctions/{id}/winner` | GET | Ver ganador |
| `/api/auctions/{id}/join` | POST | Unirse a subasta |
| `/api/auctions/{id}/bids` | POST | Hacer oferta |
| `/api/auth/login` | POST | Login |
| `/api/auth/register` | POST | Registro |
| `/api/auth/me` | GET | Obtener mi perfil |
| + todos los endpoints públicos |

**Acceso a**:
- Compra de medicamentos
- Gestión de carrito y órdenes
- Participación en subastas
- Visualización de catálogo

---

### 🚚 DELIVERY (Repartidor)
**6 endpoints en total**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/logistics/deliveries/{id}/location` | PUT | Actualizar ubicación de delivery |
| `/api/logistics/deliveries/{id}/complete` | PUT | Marcar delivery como completado |
| `/api/logistics/deliveries/active` | GET | Ver mis deliveries activos |
| `/api/logistics/deliveries/{id}` | GET | Ver detalles de delivery |
| `/api/auth/login` | POST | Login |
| `/api/auth/register` | POST | Registro |
| `/api/auth/me` | GET | Obtener mi perfil |
| + todos los endpoints públicos |

**Acceso a**:
- Gestión de deliveries personales
- Actualización de estado de envíos
- Visualización de catálogo público

---

### 🌍 PÚBLICO (Sin Autenticación)
**8 endpoints - Accesibles para TODOS**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/auth/login` | POST | Autenticarse en el sistema |
| `/api/auth/register` | POST | Crear nueva cuenta |
| `/api/medications/search` | GET | Buscar medicamentos por nombre |
| `/api/medications/branch/{branchId}/stock` | GET | Ver stock en sucursal |
| `/api/medications/branch/{branchId}/medications` | GET | Ver medicamentos por sucursal |
| `/api/medications/branches` | GET | Ver todas las sucursales |
| `/api/medications/{id}/availability/branch/{branchId}` | GET | Disponibilidad en sucursal |
| `/api/medications/{id}/availability/branches` | GET | Disponibilidad en todas sucursales |

---

## 📖 DETALLES DE CADA ENDPOINT

### 🔐 AUTH - AUTENTICACIÓN Y USUARIOS

#### 1️⃣ POST `/api/auth/login`
**Quién puede usar**: ✅ **TODOS (Público)**
- ✅ Usuario sin registrar
- ✅ Admin
- ✅ Affiliate (Cliente)
- ✅ Delivery (Repartidor)

**Descripción**: Iniciar sesión en el sistema con email y contraseña

**Request**:
```json
{
  "email": "usuario@medigo.com",
  "password": "miPassword123"
}
```

**Response** (200 OK):
```json
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@medigo.com",
  "role": "AFFILIATE",
  "jwt_token": "eyJhbGc...",
  "token_type": "Bearer"
}
```

---

#### 2️⃣ POST `/api/auth/register`
**Quién puede usar**: ✅ **TODOS (Público)**
- ✅ Usuario sin registrar
- ✅ Admin
- ✅ Affiliate (Cliente)
- ✅ Delivery (Repartidor)

**Descripción**: Registrarse en el sistema

**Request**:
```json
{
  "name": "Juan Pérez",
  "email": "juan@medigo.com",
  "password": "miPassword123",
  "role": "AFFILIATE"
}
```

**Response** (201 Created):
```json
{
  "id": 10,
  "name": "Juan Pérez",
  "email": "juan@medigo.com",
  "role": "AFFILIATE",
  "createdAt": "2026-04-03T21:10:00",
  "message": "Usuario registrado exitosamente"
}
```

---

#### 3️⃣ GET `/api/auth/me?user_id={id}`
**Quién puede usar**: ✅ **Cualquiera autenticado**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT
- ✅ Affiliate con JWT
- ✅ Delivery con JWT

**Descripción**: Obtener mis datos del perfil

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
{
  "user_id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN",
  "active": true
}
```

---

#### 4️⃣ GET `/api/auth/{id}`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**
- ✅ Admin con JWT → **200 OK**

**Descripción**: Obtener datos de cualquier usuario (solo admin puede)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
```

**Response** (200 OK):
```json
{
  "user_id": 5,
  "username": "cliente1",
  "email": "cliente1@medigo.com",
  "role": "AFFILIATE",
  "active": true
}
```

---

#### 5️⃣ GET `/api/auth/email/{email}`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**
- ✅ Admin con JWT → **200 OK**

**Descripción**: Buscar usuario por email (solo admin)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
```

**Response** (200 OK):
```json
{
  "user_id": 5,
  "username": "cliente1",
  "email": "cliente1@medigo.com",
  "role": "AFFILIATE"
}
```

---

### 📦 CATALOG - MEDICAMENTOS Y SUCURSALES

#### 6️⃣ GET `/api/medications/search?name={texto}`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Buscar medicamentos por nombre (sin autenticación requerida)

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Paracetamol 500mg",
    "description": "Analgésico",
    "unit": "tableta",
    "price": 5000.0
  }
]
```

---

#### 7️⃣ GET `/api/medications/branch/{branchId}/stock`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Ver medicamentos disponibles en una sucursal

**Response** (200 OK):
```json
[
  {
    "medicationId": 1,
    "medicationName": "Paracetamol 500mg",
    "branchId": 1,
    "quantity": 50,
    "isAvailable": true
  }
]
```

---

#### 8️⃣ GET `/api/medications/branch/{branchId}/medications`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Ver medicamentos de una sucursal

**Response** (200 OK):
```json
[
  {
    "medicationId": 1,
    "medicationName": "Ibuprofeno 400mg",
    "description": "Antiinflamatorio",
    "unit": "Caja x30",
    "quantity": 150
  }
]
```

---

#### 9️⃣ GET `/api/medications/branches`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Ver todas las sucursales y sus medicamentos

**Response** (200 OK):
```json
[
  {
    "branchId": 1,
    "branchName": "Sucursal Centro",
    "address": "Calle 10 # 5-20",
    "latitude": 4.7216,
    "longitude": -74.04499,
    "medications": [...]
  }
]
```

---

#### 🔟 GET `/api/medications/{id}/availability/branch/{branchId}`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Disponibilidad de medicamento en sucursal específica

**Response** (200 OK):
```json
{
  "branchId": 1,
  "quantity": 25,
  "isAvailable": true,
  "availabilityStatus": "Disponible"
}
```

---

#### 1️⃣1️⃣ GET `/api/medications/{id}/availability/branches`
**Quién puede usar**: ✅ **TODOS (Público)**

**Descripción**: Disponibilidad de medicamento en todas sucursales

**Response** (200 OK):
```json
{
  "medicationId": 5,
  "medicationName": "Paracetamol 500mg",
  "availabilityByBranch": [...],
  "totalAvailable": 150,
  "branchesWithStock": 5
}
```

---

#### 1️⃣2️⃣ POST `/api/medications`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**
- ✅ Admin con JWT → **201 Created**

**Descripción**: Crear nuevo medicamento (solo administrador)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "name": "Paracetamol 500mg",
  "description": "Analgésico y antipirético",
  "unit": "tableta",
  "price": 5000.0,
  "branchId": 1,
  "initialStock": 100
}
```

**Response** (201 Created):
```json
{
  "id": 10,
  "name": "Paracetamol 500mg",
  "description": "Analgésico y antipirético",
  "unit": "tableta",
  "price": 5000.0
}
```

---

#### 1️⃣3️⃣ PUT `/api/medications/{medicationId}/branch/{branchId}/stock`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**
- ✅ Admin con JWT → **204 No Content**

**Descripción**: Actualizar stock de medicamento (solo admin)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "medicationId": 1,
  "quantity": 75
}
```

**Response** (204 No Content) - Sin body

---

### 🛒 ORDERS - CARRITO Y ÓRDENES

#### 1️⃣4️⃣ POST `/api/orders/cart/add`
**Quién puede usar**: ✅ **Solo AFFILIATE (Cliente)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ✅ Affiliate con JWT → **201 Created**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Agregar medicamento al carrito (solo clientes)

**Headers requeridos**:
```
Authorization: Bearer <affiliate_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "affiliateId": 2,
  "branchId": 1,
  "medicationId": 5,
  "quantity": 2
}
```

**Response** (201 Created):
```json
{
  "cartId": 42,
  "affiliateId": 2,
  "items": [
    {
      "medicationId": 5,
      "quantity": 2,
      "unitPrice": 5000.0,
      "subtotal": 10000.0
    }
  ],
  "totalPrice": 10000.0,
  "message": "Medicamento agregado al carrito"
}
```

---

#### 1️⃣5️⃣ GET `/api/orders/cart?affiliateId={id}&branchId={id}`
**Quién puede usar**: ✅ **Solo AFFILIATE (Cliente)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Ver mi carrito (solo mi carrito)

**Headers requeridos**:
```
Authorization: Bearer <affiliate_jwt_token>
```

**Response** (200 OK):
```json
{
  "cartId": 42,
  "affiliateId": 2,
  "items": [...],
  "totalPrice": 10000.0
}
```

---

#### 1️⃣6️⃣ POST `/api/orders`
**Quién puede usar**: ✅ **Solo AFFILIATE (Cliente)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ✅ Affiliate con JWT → **201 Created**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Crear orden (solo clientes)

**Headers requeridos**:
```
Authorization: Bearer <affiliate_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "affiliateId": 2,
  "branchId": 1,
  "addressLat": 4.6452,
  "addressLng": -74.0505,
  "notes": "Dejar en portería"
}
```

**Response** (201 Created):
```json
{
  "id": 7,
  "affiliateId": 2,
  "branchId": 1,
  "status": "PENDING",
  "totalAmount": 0.0,
  "items": [],
  "createdAt": "2026-04-03T14:00:00",
  "message": "Orden creada exitosamente"
}
```

---

#### 1️⃣7️⃣ POST `/api/orders/{branchId}/confirm?affiliateId={id}`
**Quién puede usar**: ✅ **Solo AFFILIATE (Cliente)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Confirmar orden con dirección de entrega

**Headers requeridos**:
```
Authorization: Bearer <affiliate_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "street": "Calle 10",
  "streetNumber": "50-20",
  "city": "Bogotá",
  "commune": "Centro",
  "latitude": 4.711,
  "longitude": -74.0721
}
```

**Response** (200 OK):
```json
{
  "id": 100,
  "orderNumber": "ORD-2026-000123",
  "affiliateId": 2,
  "status": "CONFIRMED",
  "totalPrice": 75000.0,
  "items": [...]
}
```

---

### 🚚 LOGISTICS - ENTREGAS

#### 1️⃣8️⃣ PUT `/api/logistics/deliveries/{id}/location`
**Quién puede usar**: ✅ **Solo DELIVERY (Repartidor)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ❌ Affiliate → **403 Forbidden**
- ✅ Delivery con JWT → **200 OK**

**Descripción**: Actualizar mi ubicación durante entrega

**Headers requeridos**:
```
Authorization: Bearer <delivery_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "latitude": 4.711,
  "longitude": -74.0721,
  "address": "Calle 10, Bogotá",
  "notes": "Llegando en 2 minutos"
}
```

**Response** (200 OK):
```json
{
  "id": 1,
  "status": "IN_ROUTE",
  "lastLocation": "Calle 10, Bogotá"
}
```

---

#### 1️⃣9️⃣ PUT `/api/logistics/deliveries/{id}/complete`
**Quién puede usar**: ✅ **Solo DELIVERY (Repartidor)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ❌ Affiliate → **403 Forbidden**
- ✅ Delivery con JWT → **200 OK**

**Descripción**: Marcar entrega como completada

**Headers requeridos**:
```
Authorization: Bearer <delivery_jwt_token>
```

**Response** (200 OK):
```json
{
  "id": 1,
  "orderId": 100,
  "deliveryPersonId": 5,
  "status": "DELIVERED",
  "completedAt": "2026-04-03T15:30:00"
}
```

---

#### 2️⃣0️⃣ GET `/api/logistics/deliveries/active?deliveryPersonId={id}`
**Quién puede usar**: ✅ **Solo DELIVERY (Repartidor)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ❌ Affiliate → **403 Forbidden**
- ✅ Delivery con JWT → **200 OK**

**Descripción**: Ver mis entregas activas

**Headers requeridos**:
```
Authorization: Bearer <delivery_jwt_token>
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "orderId": 100,
    "status": "IN_ROUTE",
    "assignedAt": "2026-04-02T14:30:00"
  },
  {
    "id": 2,
    "orderId": 101,
    "status": "PENDING",
    "assignedAt": "2026-04-03T08:00:00"
  }
]
```

---

#### 2️⃣1️⃣ GET `/api/logistics/deliveries/{id}?deliveryPersonId={id}`
**Quién puede usar**: ✅ **Solo DELIVERY (Repartidor)**
- ❌ Sin autenticación → **401 Unauthorized**
- ❌ Admin → **403 Forbidden**
- ❌ Affiliate → **403 Forbidden**
- ✅ Delivery con JWT → **200 OK**

**Descripción**: Ver detalles de mi entrega específica

**Headers requeridos**:
```
Authorization: Bearer <delivery_jwt_token>
```

**Response** (200 OK):
```json
{
  "id": 1,
  "orderId": 100,
  "deliveryPersonId": 5,
  "status": "IN_ROUTE",
  "assignedAt": "2026-04-02T14:30:00",
  "customerAddress": "Calle 10, Bogotá"
}
```

---

#### 2️⃣2️⃣ POST `/api/logistics/deliveries/assign`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Asignar delivery a repartidor (solo admin)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "deliveryPersonId": 5,
  "orderId": 100
}
```

**Response** (200 OK):
```json
{
  "id": 1,
  "orderId": 100,
  "deliveryPersonId": 5,
  "status": "ASSIGNED"
}
```

---

### 🏆 AUCTIONS - SUBASTAS

#### 2️⃣3️⃣ POST `/api/auctions`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **201 Created**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Crear nueva subasta (solo admin)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "medicationId": 1,
  "branchId": 1,
  "basePrice": 10000.0,
  "startTime": "2026-04-04T10:00:00",
  "endTime": "2026-04-04T12:00:00",
  "closureType": "FIXED_TIME"
}
```

**Response** (201 Created):
```json
{
  "id": 10,
  "medicationId": 1,
  "branchId": 1,
  "basePrice": 10000.0,
  "status": "SCHEDULED"
}
```

---

#### 2️⃣4️⃣ PUT `/api/auctions/{id}`
**Quién puede usar**: ✅ **Solo ADMIN**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ❌ Affiliate → **403 Forbidden**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Editar subasta (solo admin)

**Headers requeridos**:
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "basePrice": 15000.0,
  "startTime": "2026-04-04T11:00:00",
  "endTime": "2026-04-04T13:00:00"
}
```

**Response** (200 OK):
```json
{
  "id": 10,
  "basePrice": 15000.0,
  "status": "SCHEDULED"
}
```

---

#### 2️⃣5️⃣ GET `/api/auctions/{id}`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Ver detalles de subasta

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
{
  "id": 10,
  "medicationId": 1,
  "medicationName": "Paracetamol 500mg",
  "basePrice": 10000.0,
  "status": "ACTIVE",
  "startTime": "2026-04-04T10:00:00",
  "endTime": "2026-04-04T12:00:00",
  "remainingSeconds": 3600
}
```

---

#### 2️⃣6️⃣ GET `/api/auctions/active`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Ver todas las subastas activas

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
[
  {
    "id": 10,
    "medicationName": "Paracetamol 500mg",
    "basePrice": 10000.0,
    "status": "ACTIVE"
  },
  {
    "id": 11,
    "medicationName": "Ibuprofeno 400mg",
    "basePrice": 8000.0,
    "status": "ACTIVE"
  }
]
```

---

#### 2️⃣7️⃣ GET `/api/auctions/{id}/bids`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Ver ofertas de una subasta

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "auctionId": 10,
    "userId": 2,
    "userName": "cliente1",
    "amount": 12000.0,
    "placedAt": "2026-04-04T10:15:00"
  },
  {
    "id": 2,
    "auctionId": 10,
    "userId": 3,
    "userName": "cliente2",
    "amount": 15000.0,
    "placedAt": "2026-04-04T10:25:00"
  }
]
```

---

#### 2️⃣8️⃣ GET `/api/auctions/{id}/winner`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **200 OK**
- ✅ Affiliate con JWT → **200 OK**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Ver ganador de la subasta

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Response** (200 OK):
```json
{
  "auctionId": 10,
  "winnerId": 3,
  "winnerName": "cliente2",
  "winningAmount": 15000.0
}
```

O si aún no hay ganador: **204 No Content**

---

#### 2️⃣9️⃣ POST `/api/auctions/{id}/join`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **204 No Content**
- ✅ Affiliate con JWT → **204 No Content**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Unirse a una subasta

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
```

**Query params**:
- `userId`: ID del usuario que se une

**Response** (204 No Content) - Sin body

---

#### 3️⃣0️⃣ POST `/api/auctions/{id}/bids`
**Quién puede usar**: ✅ **ADMIN + AFFILIATE**
- ❌ Sin autenticación → **401 Unauthorized**
- ✅ Admin con JWT → **201 Created**
- ✅ Affiliate con JWT → **201 Created**
- ❌ Delivery → **403 Forbidden**

**Descripción**: Hacer oferta en una subasta

**Headers requeridos**:
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request**:
```json
{
  "userId": 2,
  "userName": "cliente1",
  "amount": 18000.0
}
```

**Response** (201 Created):
```json
{
  "id": 3,
  "auctionId": 10,
  "userId": 2,
  "userName": "cliente1",
  "amount": 18000.0,
  "placedAt": "2026-04-04T10:35:00"
}
```

---

## 📋 MATRIZ RÁPIDA DE REFERENCIA

### Por Rol

**ADMIN (16 endpoints)**
- Auth: login, register, me, {id}, email/{email}
- Catalog: search, branches, medicamentos (GET), POST, PUT stock
- Logistics: assign
- Auctions: POST, PUT, GET, active, bids, winner, join, POST bids

**AFFILIATE (10 endpoints)**
- Auth: login, register, me
- Orders: add cart, GET cart, POST order, confirm
- Auctions: GET {id}, active, bids, winner, join, POST bids

**DELIVERY (6 endpoints)**
- Auth: login, register, me
- Logistics: location, complete, active, {id}

**PÚBLICO (8 endpoints)**
- Auth: login, register
- Catalog: search, branches, medications, stock, availability/*

---

## 🔒 REGLAS DE SEGURIDAD

1. **Todos los endpoints protegidos requieren JWT** en header `Authorization: Bearer <token>`
2. **Sin JWT en endpoint protegido** → **401 Unauthorized**
3. **JWT válido pero rol incorrecto** → **403 Forbidden**
4. **Endpoints públicos** → No requieren JWT
5. **Autenticado** significa cualquier rol con JWT válido

---

## ✅ RESUMEN FINAL

- **Total endpoints**: 30
- **Públicos**: 8 (sin JWT requerido)
- **Solo ADMIN**: 8
- **Solo AFFILIATE**: 4
- **Solo DELIVERY**: 4
- **ADMIN + AFFILIATE**: 6

**Actualizado**: 2026-04-03
**Completitud**: 100% - Todos los endpoints documentados con roles

