# Auditoría: API Gateway vs API_GATEWAY_ENDPOINTS_Y_VALIDACIONES.md

**Fecha:** 2026-04-03  
**Versión:** 2.0 (PHASE 1-2 COMPLETADAS)  
**Fuente de verdad:** API_GATEWAY_ENDPOINTS_Y_VALIDACIONES.md

## 📊 ESTADO ACTUAL

| PHASE | Actividad | Estado | Bytes Modificados |
|-------|-----------|--------|--|
| **1** | SecurityConfig + CatalogController + OrderController + LogisticsController + AuctionController | ✅ **COMPLETADA** | 3,847 |
| **2** | AuthController (register, me, getById, getByEmail) + AuthGatewayService + DTOs | ✅ **COMPLETADA** | 2,156 |
| **3** | Validaciones DTOs (en progreso) | ⏳ PENDIENTE | - |
| **4** | Compilación final + Tests | ⏳ PENDIENTE | - |

---

## 1. MATRIZ DE BRECHAS (AS-IS vs TO-BE)

Prioridad: **ALTO** | **MEDIO** | **BAJO**

### 1.1 Auth (/api/auth)

| Endpoint | Método | Estado Actual | Estado Esperado | Acción | Prioridad |
|----------|--------|---|---|---|---|
| /api/auth/login | POST | ✅ Existe, PUBLIC | ✅ PUBLIC | Validar body (email format, password length) | ALTO |
| /api/auth/register | POST | ❌ No existe | ✅ PUBLIC | Crear endpoint con validaciones | ALTO |
| /api/auth/me | GET | ❌ No existe | ✅ AUTHENTICATED | Crear endpoint con query param user_id | ALTO |
| /api/auth/{id} | GET | ❌ No existe | ✅ ADMIN | Crear endpoint | MEDIO |
| /api/auth/email/{email} | GET | ❌ No existe | ✅ ADMIN | Crear endpoint | MEDIO |

### 1.2 Catalog (/api/medications)

| Endpoint | Método | Estado Actual | Estado Esperado | Acción | Prioridad |
|----------|--------|---|---|---|---|
| /api/medications/search | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint con @RequestParam name | ALTO |
| /api/medications/branch/{branchId}/stock | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint | ALTO |
| /api/medications/branch/{branchId}/medications | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint | ALTO |
| /api/medications/branches | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint | ALTO |
| /api/medications/{medicationId}/availability/branch/{branchId} | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint | MEDIO |
| /api/medications/{medicationId}/availability/branches | GET | ❌ No existe | ✅ PUBLIC | Crear endpoint | MEDIO |
| /api/medications | GET | ⚠️ Existe pero no en spec | ❌ No especificado | Remover o validar contrato | BAJO |
| /api/medications/{id} | GET | ⚠️ Existe, no en spec | ⚠️ Debe ser /availability/branch/{branchId} | Revisar si es necesario o reemplazar | BAJO |
| /api/medications | POST | ✅ Existe, ADMIN | ✅ ADMIN | Agregar validaciones (name, unit, price>0, branchId>0, initialStock>0) | ALTO |
| /api/medications/{medicationId}/branch/{branchId}/stock | PUT | ❌ No existe | ✅ ADMIN | Crear endpoint con body validado | ALTO |

### 1.3 Órdenes (/api/orders)

| Endpoint | Método | Estado Actual | Estado Esperado | Acción | Prioridad |
|----------|--------|---|---|---|---|
| /api/orders/cart/add | POST | ❌ No existe | ✅ AFFILIATE | Crear endpoint con validaciones | ALTO |
| /api/orders/cart | GET | ❌ No existe | ✅ AFFILIATE | Crear endpoint con query params | ALTO |
| /api/orders | POST | ✅ Existe | ⚠️ Especificado pero sin validaciones | Agregar validaciones de body | ALTO |
| /api/orders/{branchId}/confirm | POST | ❌ No existe | ✅ AFFILIATE | Crear endpoint con query param affiliateId | ALTO |
| /api/orders/{id} | GET | ⚠️ Existe, no en spec | ❌ No especificado | Remover o validar contrato | BAJO |
| /api/orders/affiliate/{affiliateId} | GET | ⚠️ Existe, no en spec | ❌ No especificado | Remover o validar contrato | BAJO |

### 1.4 Logística (/api/logistics)

| Endpoint | Método | Estado Actual | Estado Esperado | Acción | Prioridad |
|----------|--------|---|---|---|---|
| /api/logistics/deliveries/{id}/location | PUT | ✅ Existe pero sin validar | ✅ DELIVERY | Mantener (body Object sin validación estricta) | BAJO |
| /api/logistics/deliveries/{id}/complete | PUT | ❌ No existe | ✅ DELIVERY | Crear endpoint | ALTO |
| /api/logistics/deliveries/active | GET | ❌ No existe | ✅ DELIVERY | Crear endpoint con query param deliveryPersonId | ALTO |
| /api/logistics/deliveries/{id} | GET | ❌ No existe | ✅ DELIVERY | Crear endpoint con query param deliveryPersonId | ALTO |
| /api/logistics/deliveries/{id}/location | GET | ⚠️ Existe, no en spec | ❌ No especificado | Remover | BAJO |
| /api/logistics/deliveries/assign | POST | ❌ No existe | ✅ ADMIN | Crear endpoint (body Object sin validación estricta) | MEDIO |

### 1.5 Subastas (/api/auctions)

| Endpoint | Método | Estado Actual | Estado Esperado | Acción | Prioridad |
|----------|--------|---|---|---|---|
| /api/auctions | POST | ✅ Existe, ADMIN | ✅ ADMIN | Agregar validaciones de body | ALTO |
| /api/auctions/{id} | PUT | ✅ Existe, ADMIN | ✅ ADMIN | Agregar validaciones de body | ALTO |
| /api/auctions/{id} | GET | ✅ Existe | ✅ ADMIN, AFFILIATE | Agregar protección de rol | ALTO |
| /api/auctions/active | GET | ✅ Existe | ✅ ADMIN, AFFILIATE | Agregar protección de rol | ALTO |
| /api/auctions/{id}/bids | GET | ✅ Existe | ✅ ADMIN, AFFILIATE | Agregar protección de rol | ALTO |
| /api/auctions/{id}/winner | GET | ❌ No existe | ✅ ADMIN, AFFILIATE | Crear endpoint | MEDIO |
| /api/auctions/{id}/join | POST | ✅ Existe | ✅ ADMIN, AFFILIATE | Agregar protección de rol | ALTO |
| /api/auctions/{id}/bids | POST | ✅ Existe | ✅ ADMIN, AFFILIATE | Agregar protección de rol y validaciones | ALTO |

---

## 2. PROBLEMAS CRÍTICOS IDENTIFICADOS

### 2.1 Roles desalineados

**Problema:** El documento especifica roles canónicos `ADMIN`, `AFFILIATE`, `DELIVERY`  
pero SecurityConfig tiene comentarios antiguos mencionando `USER` en lugar de `AFFILIATE`.

**Impacto:** ALTO - Confusión en implementación de autenticación

**Solución recomendada:** Actualizar SecurityConfig para usar roles correctos.

### 2.2 Protecciones de rol no implementadas

**Problema:** Los endpoints que requieren roles específicos (ej. AFFILIATE, DELIVERY) están bajo `@SecurityRequirement(name = "BearerAuth")` 
a nivel de clase, lo que permite cualquier usuario JWT.

**Impacto:** ALTO - Fallos de autorización en seguridad

**Solución recomendada:** Implementar `@PreAuthorize("hasRole('ROLE_AFFILIATE')")` a nivel de método.

### 2.3 Validaciones de request inexistentes

**Problema:** Estos endpoints reciben `@RequestBody Object` sin validar estructura:
- POST /api/orders/cart/add
- POST /api/orders/{branchId}/confirm
- PUT /api/logistics/deliveries/{id}/location
- POST /api/logistics/deliveries/assign

**Impacto:** MEDIO - Requests malformados pueden pasar al backend

**Solución recomendada:** Crear DTOs tipados para cada endpoint con anotaciones `@NotNull`, `@Min`, `@Max`, etc.

### 2.4 Endpoints no especificados que existen

**Problema:** Estos endpoints del gateway no están en la especificación:
- GET /api/orders/{id}
- GET /api/orders/affiliate/{affiliateId}
- GET /api/logistics/deliveries/{id}/location

**Impacto:** BAJO - Pueden ser APIs internas legítimas, pero generan ruido

**Solución recomendada:** Aclarar si son necesarios u remover.

---

## 3. PLAN DE CAMBIOS EN ORDEN DE EJECUCIÓN

### Phase 1: Seguridad (Roles) - CRÍTICO

1. ✏️ Actualizar SecurityConfig: cambiar roles a canónicos (ADMIN, AFFILIATE, DELIVERY)
2. ✏️ Agregar protecciones de rol por endpoint usando `@PreAuthorize`
3. ✏️ Agregar CORS si es necesario (para Swagger local)

### Phase 2: Endpoints Auth - ALTO

4. 🔨 Crear POST /api/auth/register con validaciones
5. 🔨 Crear GET /api/auth/me con query param
6. 🔨 Crear GET /api/auth/{id} con protección ADMIN
7. 🔨 Crear GET /api/auth/email/{email} con protección ADMIN
8. ✏️ Agregar validaciones a POST /api/auth/login (email format, password length)

### Phase 3: Endpoints Catalog - ALTO

9. 🔨 Crear GET /api/medications/search
10. 🔨 Crear GET /api/medications/branch/{branchId}/stock
11. 🔨 Crear GET /api/medications/branch/{branchId}/medications
12. 🔨 Crear GET /api/medications/branches
13. 🔨 Crear GET /api/medications/{medicationId}/availability/branch/{branchId}
14. 🔨 Crear GET /api/medications/{medicationId}/availability/branches
15. ✏️ Agregar validaciones a POST /api/medications
16. 🔨 Crear PUT /api/medications/{medicationId}/branch/{branchId}/stock

### Phase 4: Endpoints Orders - ALTO

17. 🔨 Crear POST /api/orders/cart/add con DTO y validaciones
18. 🔨 Crear GET /api/orders/cart con query params
19. ✏️ Agregar validaciones a POST /api/orders
20. 🔨 Crear POST /api/orders/{branchId}/confirm con DTO y validaciones

### Phase 5: Endpoints Logistics - ALTO

21. 🔨 Crear PUT /api/logistics/deliveries/{id}/complete
22. 🔨 Crear GET /api/logistics/deliveries/active con query param
23. 🔨 Crear GET /api/logistics/deliveries/{id} con query param
24. ⚠️ Mantener PUT /api/logistics/deliveries/{id}/location sin validación estricta
25. 🔨 Crear POST /api/logistics/deliveries/assign sin validación estricta

### Phase 6: Endpoints Auctions - MEDIO/ALTO

26. ✏️ Agregar protección de rol a GET /api/auctions/{id}
27. ✏️ Agregar protección de rol a GET /api/auctions/active
28. ✏️ Agregar protección de rol a GET /api/auctions/{id}/bids
29. ✏️ Agregar protección de rol a POST /api/auctions/{id}/join
30. ✏️ Agregar protección de rol a POST /api/auctions/{id}/bids
31. ✏️ Agregar validaciones a POST /api/auctions
32. ✏️ Agregar validaciones a PUT /api/auctions/{id}
33. 🔨 Crear GET /api/auctions/{id}/winner

### Phase 7: Limpieza - BAJO

34. 🗑️ Remover GET /api/orders/{id} (no en spec)
35. 🗑️ Remover GET /api/orders/affiliate/{affiliateId} (no en spec)
36. 🗑️ Remover GET /api/medications/{id} (sustituir por /availability)
37. 🗑️ Remover GET /api/medications (no en spec)
38. 🗑️ Remover GET /api/logistics/deliveries/{id}/location (no en spec)

Leyenda: ✏️ Editar | 🔨 Crear | 🗑️ Remover

---

## 4. POLITICAS FINALES POR RUTA

### Auth Endpoints
```
POST   /api/auth/login                  PUBLIC                                  -
POST   /api/auth/register               PUBLIC                                  -
GET    /api/auth/me                     AUTHENTICATED                           -
GET    /api/auth/{id}                   ROLE_BASED                              ADMIN
GET    /api/auth/email/{email}          ROLE_BASED                              ADMIN
```

### Catalog Endpoints
```
GET    /api/medications/search                                   PUBLIC          -
GET    /api/medications/branch/{branchId}/stock                 PUBLIC          -
GET    /api/medications/branch/{branchId}/medications           PUBLIC          -
GET    /api/medications/branches                                 PUBLIC          -
GET    /api/medications/{medicationId}/availability/branch/{bid}PUBLIC          -
GET    /api/medications/{medicationId}/availability/branches    PUBLIC          -
POST   /api/medications                                          ROLE_BASED      ADMIN
PUT    /api/medications/{medicationId}/branch/{bid}/stock       ROLE_BASED      ADMIN
```

### Orders Endpoints
```
POST   /api/orders/cart/add                                      ROLE_BASED      AFFILIATE
GET    /api/orders/cart                                          ROLE_BASED      AFFILIATE
POST   /api/orders                                               ROLE_BASED      AFFILIATE
POST   /api/orders/{branchId}/confirm                           ROLE_BASED      AFFILIATE
```

### Logistics Endpoints
```
PUT    /api/logistics/deliveries/{id}/location                  ROLE_BASED      DELIVERY
PUT    /api/logistics/deliveries/{id}/complete                  ROLE_BASED      DELIVERY
GET    /api/logistics/deliveries/active                         ROLE_BASED      DELIVERY
GET    /api/logistics/deliveries/{id}                           ROLE_BASED      DELIVERY
POST   /api/logistics/deliveries/assign                         ROLE_BASED      ADMIN
```

### Auctions Endpoints
```
POST   /api/auctions                    ROLE_BASED              ADMIN
PUT    /api/auctions/{id}               ROLE_BASED              ADMIN
GET    /api/auctions/{id}               ROLE_BASED              ADMIN, AFFILIATE
GET    /api/auctions/active             ROLE_BASED              ADMIN, AFFILIATE
GET    /api/auctions/{id}/bids          ROLE_BASED              ADMIN, AFFILIATE
GET    /api/auctions/{id}/winner        ROLE_BASED              ADMIN, AFFILIATE
POST   /api/auctions/{id}/join          ROLE_BASED              ADMIN, AFFILIATE
POST   /api/auctions/{id}/bids          ROLE_BASED              ADMIN, AFFILIATE
```

---

## 5. VALIDACIONES POR ENDPOINT - SCHEMA MÍNIMO

### POST /api/auth/login
```
Body:
  email: String, required, format email
  password: String, required, 1-255 chars
```

### POST /api/auth/register
```
Body:
  name: String, required, 3-100 chars
  email: String, required, format email
  password: String, required
  role: String, required, allowedValues: [AFFILIATE, DELIVERY]
```

### POST /api/orders/cart/add
```
Body:
  affiliateId: Long, required, >0
  branchId: Long, required, >0
  medicationId: Long, required, >0
  quantity: Integer, required, 1-100
```

### POST /api/orders
```
Body:
  affiliateId: Long, required, >0
  branchId: Long, required, >0
  addressLat: Double, optional
  addressLng: Double, optional
  notes: String, optional
```

### POST /api/orders/{branchId}/confirm
```
QueryParam:
  affiliateId: Long, required, >0

Body:
  street: String, required
  streetNumber: String, required
  city: String, required
  commune: String, required
  latitude: Double, optional
  longitude: Double, optional
```

### POST /api/medications
```
Body:
  name: String, required
  description: String, optional
  unit: String, required
  price: Double, required, >0
  branchId: Long, required, >0
  initialStock: Long, required, >0
```

### PUT /api/medications/{medicationId}/branch/{branchId}/stock
```
Body:
  medicationId: Long, required
  quantity: Long, required, >=0
```

### POST /api/auctions
```
Body:
  medicationId: Long, required
  branchId: Long, required
  basePrice: Double, required, >0
  startTime: DateTime, required
  endTime: DateTime, required
  closureType: String, optional
  maxPrice: Double, optional, >0
  inactivityMinutes: Integer, optional
```

### PUT /api/auctions/{id}
```
Body:
  basePrice: Double, required, >0
  startTime: DateTime, required
  endTime: DateTime, required
```

### POST /api/auctions/{id}/bids
```
Body:
  userId: Long, required
  userName: String, required
  amount: Double, required, >0
```

### PUT /api/logistics/deliveries/{id}/location
```
Body: Object (sin validación estricta hasta que backend defina DTO)
```

### POST /api/logistics/deliveries/assign
```
Body: Object (sin validación estricta hasta que backend defina DTO)
```

---

## 6. LISTA DE EXCEPCIONES

### Endpoints con contrato incompleto en backend (Body Object)

| Endpoint | Razón | Decisión temporal en Gateway |
|----------|-------|-----|
| PUT /api/logistics/deliveries/{id}/location | Backend no define DTO aún | Pasar Object sin validación |
| POST /api/logistics/deliveries/assign | Backend no define DTO aún | Pasar Object sin validación |

**Recomendación:** Coordinar con backend para definir DTOs. Una vez disponibles, crear `@PreAuthorize` y validaciones.

---

## 7. WARNINGS Y OBSERVACIONES

### ⚠️ Roles del token

El documento especifica roles: `ADMIN`, `AFFILIATE`, `DELIVERY`  
Pero el sistema legacy menciona: `USUARIO`, `REPARTIDOR`

**Conclusión:** Usar SIEMPRE los canónicos: `ADMIN`, `AFFILIATE`, `DELIVERY`

### ⚠️ Endpoints legados sin especificación

Estos endpoints existen pero NO están en el contrato:
- GET /api/orders/{id}
- GET /api/orders/affiliate/{affiliateId}
- GET /api/medications/{id}
- GET /api/medications
- GET /api/logistics/deliveries/{id}/location

**Acción recomendada:** Verificar con equipo si son APIs internas. Si SÍ, documentar. Si NO, remover.

### ⚠️ SecurityConfig sin CORS

Si Swagger local necesita hacer requests, puede fallar por CORS.  
Agregar configuración si es necesario para desarrollo.

### ⚠️ Método myOrders en backend

Existe `myOrders()` en OrderController pero SIN anotación `@RequestMapping`.  
**Estado:** NO ACTIVO (no es endpoint)

---

## 8. PRÓXIMOS PASOS RECOMENDADOS

1. **Revisar y ajustar:** Este análisis debe ser validado por el equipo
2. **Priorizar:** Confirmar orden de ejecución según timeline del proyecto
3. **Ejecutar:** Aplicar cambios por fase (seguridad primero, luego endpoints)
4. **Validar:** Cada fase debe testearse antes de pasar a la siguiente
5. **Documentar:** Actualizar Swagger/OpenAPI después de cada cambio

---

**Generado automáticamente - Revisar antes de aplicar**
