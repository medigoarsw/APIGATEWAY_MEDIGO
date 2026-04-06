# 🚀 GUÍA RÁPIDA - ENDPOINTS POR ROL
## MediGo API Gateway - Acceso Rápido

---

## 👑 ADMIN (Administrador del Sistema)
**16 endpoints totales**

```
✅ AUTH (5)
  POST   /api/auth/login              (También: cualquiera)
  POST   /api/auth/register           (También: cualquiera)
  GET    /api/auth/me                 (También: Affiliate, Delivery)
  GET    /api/auth/{id}               (Solo ADMIN)
  GET    /api/auth/email/{email}      (Solo ADMIN)

✅ CATALOG (8)
  GET    /api/medications/search              (También: todos)
  GET    /api/medications/branches            (También: todos)
  GET    /api/medications/branch/{id}/stock  (También: todos)
  GET    /api/medications/{id}/availability/* (También: todos)
  POST   /api/medications                     (Solo ADMIN)
  PUT    /api/medications/{id}/branch/{id}/stock (Solo ADMIN)

✅ LOGISTICS (1)
  POST   /api/logistics/deliveries/assign     (Solo ADMIN)

✅ AUCTIONS (8)
  POST   /api/auctions/{id}           (Solo ADMIN)
  PUT    /api/auctions/{id}           (Solo ADMIN)
  GET    /api/auctions/{id}           (También: Affiliate)
  GET    /api/auctions/active         (También: Affiliate)
  GET    /api/auctions/{id}/bids      (También: Affiliate)
  GET    /api/auctions/{id}/winner    (También: Affiliate)
  POST   /api/auctions/{id}/join      (También: Affiliate)
  POST   /api/auctions/{id}/bids      (También: Affiliate)
```

**Lo que puede hacer**:
- Crear/editar medicamentos
- Gestionar stock
- Crear/editar subastas
- Ver cualquier usuario
- Asignar deliveries
- Participar en subastas

---

## 👤 AFFILIATE (Cliente/Paciente)
**13 endpoints totales**

```
✅ AUTH (3)
  POST   /api/auth/login              (También: todos)
  POST   /api/auth/register           (También: todos)
  GET    /api/auth/me                 (También: Admin, Delivery)

✅ CATALOG (6 - Lectura)
  GET    /api/medications/search
  GET    /api/medications/branches
  GET    /api/medications/branch/{id}/stock
  GET    /api/medications/{id}/availability/branch/{id}
  GET    /api/medications/{id}/availability/branches

✅ ORDERS (4 - Solo suyo)
  POST   /api/orders/cart/add
  GET    /api/orders/cart
  POST   /api/orders
  POST   /api/orders/{branchId}/confirm

✅ AUCTIONS (4 - Participar)
  GET    /api/auctions/{id}
  GET    /api/auctions/active
  GET    /api/auctions/{id}/bids
  POST   /api/auctions/{id}/join
  POST   /api/auctions/{id}/bids
  GET    /api/auctions/{id}/winner
```

**Lo que puede hacer**:
- Buscar medicamentos
- Agregar al carrito
- Crear/confirmar órdenes
- Participar en subastas
- Ver ofertas en subastas
- Consultar su perfil

---

## 🚚 DELIVERY (Repartidor)
**7 endpoints totales**

```
✅ AUTH (3)
  POST   /api/auth/login              (También: todos)
  POST   /api/auth/register           (También: todos)
  GET    /api/auth/me                 (También: Admin, Affiliate)

✅ CATALOG (6 - Lectura)
  GET    /api/medications/search
  GET    /api/medications/branches
  GET    /api/medications/branch/{id}/stock
  GET    /api/medications/{id}/availability/branch/{id}
  GET    /api/medications/{id}/availability/branches

✅ LOGISTICS (4 - Sus entregas)
  GET    /api/logistics/deliveries/active
  GET    /api/logistics/deliveries/{id}
  PUT    /api/logistics/deliveries/{id}/location
  PUT    /api/logistics/deliveries/{id}/complete
```

**Lo que puede hacer**:
- Ver su catálogo
- Ver sus entregas activas
- Actualizar ubicación
- Marcar delivery como completo
- Consultar su perfil

---

## 🌍 PÚBLICO (Sin Login)
**8 endpoints - Acceso libre**

```
✅ AUTH (2)
  POST   /api/auth/login
  POST   /api/auth/register

✅ CATALOG (6)
  GET    /api/medications/search
  GET    /api/medications/branches
  GET    /api/medications/branch/{id}/stock
  GET    /api/medications/{id}/availability/branch/{id}
  GET    /api/medications/{id}/availability/branches
  GET    /api/medications/branch/{id}/medications
```

**Lo que puede hacer**:
- Buscar medicamentos
- Ver sucursales
- Consultar disponibilidad
- Hacer login
- Registrarse

---

## 🔑 CÓDIGOS DE ESTADO HTTP

| Código | Significado | Causa |
|--------|------------|-------|
| **200** | OK | Operación exitosa |
| **201** | Created | Recurso creado |
| **204** | No Content | Éxito sin respuesta |
| **400** | Bad Request | Parámetros inválidos |
| **401** | Unauthorized | Falta JWT o inválido |
| **403** | Forbidden | Rol insuficiente ❌ |
| **404** | Not Found | Recurso no existe |
| **500** | Server Error | Error del servidor |

---

## 🎯 ERRORES COMUNES

### Error 403 Forbidden
```
❌ Causa: Tu rol no tiene permiso
✅ Solución: Verifica tu rol y usa endpoint permitido

Ejemplos:
- AFFILIATE intentando crear medicamento → 403
- DELIVERY intentando crear orden → 403
- Sin autenticación en endpoint protegido → 401 (no 403)
```

### Error 401 Unauthorized
```
❌ Causa: Falta JWT o inválido
✅ Solución: Incluye JWT en header Authorization

Header requerido:
Authorization: Bearer <tu_jwt_token>
```

### Error 500 Internal Server Error
```
❌ Causa: Error del servidor o validación fallida
✅ Solución: 
- Verifica parámetros
- Revisa si recursos existen
- Contacta admin si persiste
```

---

## 🧪 TESTING RÁPIDO

### Login como ADMIN
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@medigo.com",
    "password": "admin123"
  }'
```

### Login como AFFILIATE
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@medigo.com",
    "password": "pass123"
  }'
```

### Usar token
```bash
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 📱 FLUJOS DE USUARIO

### Flujo: Cliente compra medicamento
```
1. GET  /api/medications/search (buscar)
2. POST /api/auth/login (autenticarse como AFFILIATE)
3. POST /api/orders/cart/add (agregar al carrito)
4. GET  /api/orders/cart (ver carrito)
5. POST /api/orders (crear orden)
6. POST /api/orders/{branchId}/confirm (confirmar con dirección)
```

### Flujo: Admin crea medicamento
```
1. POST /api/auth/login (como ADMIN)
2. POST /api/medications (crear medicamento)
3. PUT  /api/medications/{id}/branch/{id}/stock (actualizar stock)
```

### Flujo: Repartidor entrega medicamento
```
1. POST /api/auth/login (como DELIVERY)
2. GET  /api/logistics/deliveries/active (ver entregas)
3. PUT  /api/logistics/deliveries/{id}/location (actualizar ubicación)
4. PUT  /api/logistics/deliveries/{id}/complete (marcar completado)
```

---

## 🔐 SEGURIDAD

**Roles en el JWT**:
- `ADMIN` - Administrador completo
- `AFFILIATE` - Cliente/Paciente
- `DELIVERY` - Repartidor

**Token JWT estructura**:
```json
{
  "user_id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN",
  "exp": 1238976000
}
```

**Siempre incluir en headers protegidos**:
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

---

## 📞 REFERENCIA RÁPIDA

| Necesito... | Endpoint | Método | Rol |
|-------------|----------|--------|-----|
| Buscar medicamentos | `/api/medications/search` | GET | Público |
| Ver mi carrito | `/api/orders/cart` | GET | AFFILIATE |
| Crear orden | `/api/orders` | POST | AFFILIATE |
| Ver mis entregas | `/api/logistics/deliveries/active` | GET | DELIVERY |
| Crear medicamento | `/api/medications` | POST | ADMIN |
| Ver usuario | `/api/auth/{id}` | GET | ADMIN |
| Ver subastas activas | `/api/auctions/active` | GET | ADMIN/AFFILIATE |
| Hacer oferta | `/api/auctions/{id}/bids` | POST | ADMIN/AFFILIATE |

---

**Última actualización**: 2026-04-03  
**Documentación completa en**: `ENDPOINTS_POR_ROL_DETALLADO.md`

