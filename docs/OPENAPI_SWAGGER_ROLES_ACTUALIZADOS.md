# 📘 Documentación OpenAPI Actualizada con Roles
## MediGo API Gateway - Anotaciones Swagger

**Fecha**: 2026-04-03  
**Status**: ✅ Compilado exitosamente

---

## 📊 CAMBIOS REALIZADOS

Se actualizaron **todas las anotaciones `@Operation`** en los 6 controladores para incluir etiquetas de rol explícitas en los summaries. Ahora aparecen en Swagger/OpenAPI de forma clara.

---

## 🔍 EJEMPLOS DE CÓMO SE VE EN SWAGGER

### ANTES (Sin etiqueta de rol)
```
POST /api/auth/login
  Summary: "Login - genera JWT"
```

### DESPUÉS (Con etiqueta de rol)
```
POST /api/auth/login
  Summary: "Login - genera JWT (PUBLIC)"
```

---

## 📋 TODOS LOS ENDPOINTS - COMO APARECEN EN SWAGGER

### 🟢 AUTH (/api/auth)

| Método | Endpoint | Summary OpenAPI |
|--------|----------|-----------------|
| POST | `/login` | `Login - genera JWT (PUBLIC)` |
| POST | `/register` | `Registrar nuevo usuario (PUBLIC)` |
| GET | `/me` | `Obtener información del usuario actual (AUTHENTICATED)` |
| GET | `/{id}` | `Obtener usuario por ID (ADMIN ONLY)` |
| GET | `/email/{email}` | `Obtener usuario por email (ADMIN ONLY)` |

### 🟢 CATALOG (/api/medications)

| Método | Endpoint | Summary OpenAPI |
|--------|----------|-----------------|
| GET | `/search?name=X` | `Buscar medicamentos por nombre (PUBLIC)` |
| GET | `/branch/{id}/stock` | `Stock de medicamentos en sucursal (PUBLIC)` |
| GET | `/branch/{id}/medications` | `Medicamentos disponibles en sucursal (PUBLIC)` |
| GET | `/branches` | `Listar sucursales con medicamentos (PUBLIC)` |
| GET | `/{id}/availability/branch/{id}` | `Verificar disponibilidad en sucursal (PUBLIC)` |
| GET | `/{id}/availability/branches` | `Disponibilidad en todas las sucursales (PUBLIC)` |
| POST | `/` | `Crear medicamento (ADMIN ONLY)` |
| PUT | `/{id}/branch/{id}/stock` | `Actualizar stock en sucursal (ADMIN ONLY)` |

### 🟢 ORDERS (/api/orders)

| Método | Endpoint | Summary OpenAPI |
|--------|----------|-----------------|
| POST | `/cart/add` | `Agregar medicamento al carrito (AFFILIATE ONLY)` |
| GET | `/cart` | `Ver carrito (AFFILIATE ONLY)` |
| POST | `/` | `Crear orden (AFFILIATE ONLY)` |
| POST | `/{id}/confirm` | `Confirmar orden (AFFILIATE ONLY)` |

### 🟢 LOGISTICS (/api/logistics)

| Método | Endpoint | Summary OpenAPI |
|--------|----------|-----------------|
| PUT | `/deliveries/{id}/location` | `Actualizar ubicación de entrega (DELIVERY ONLY)` |
| PUT | `/deliveries/{id}/complete` | `Marcar entrega como completada (DELIVERY ONLY)` |
| GET | `/deliveries/active` | `Entregas activas del repartidor (DELIVERY ONLY)` |
| GET | `/deliveries/{id}` | `Estado de entrega específica (DELIVERY ONLY)` |
| POST | `/deliveries/assign` | `Asignar entrega a repartidor (ADMIN ONLY)` |

### 🟢 AUCTIONS (/api/auctions)

| Método | Endpoint | Summary OpenAPI |
|--------|----------|-----------------|
| POST | `/` | `Crear subasta (ADMIN ONLY)` |
| PUT | `/{id}` | `Editar subasta (ADMIN ONLY)` |
| GET | `/active` | `Subastas activas (ADMIN + AFFILIATE)` |
| GET | `/{id}` | `Obtener subasta por ID (ADMIN + AFFILIATE)` |
| GET | `/{id}/bids` | `Historial de pujas (ADMIN + AFFILIATE)` |
| GET | `/{id}/winner` | `Ganador de subasta (ADMIN + AFFILIATE)` |
| POST | `/{id}/join` | `Unirse a subasta (ADMIN + AFFILIATE)` |
| POST | `/{id}/bids` | `Colocar puja (ADMIN + AFFILIATE)` |

---

## 🔐 LEYENDA DE ETIQUETAS

| Etiqueta | Significado | Acceso |
|----------|------------|--------|
| **(PUBLIC)** | Sin autenticación requerida | ✅ Cualquiera |
| **(AUTHENTICATED)** | JWT válido necesario (cualquier rol) | ✅ Con JWT válido |
| **(ADMIN ONLY)** | Solo administrador | ✅ ADMIN |
| **(AFFILIATE ONLY)** | Solo cliente/paciente | ✅ AFFILIATE |
| **(DELIVERY ONLY)** | Solo repartidor | ✅ DELIVERY |
| **(ADMIN + AFFILIATE)** | Múltiples roles permitidos | ✅ ADMIN o AFFILIATE |

---

## 📝 ARCHIVO DE CÓDIGO - ANTES Y DESPUÉS

### AuthController.java - ANTES
```java
@PostMapping("/login")
@Operation(summary = "Login - genera JWT")
public ResponseEntity<GatewayResponse<LoginResponse>> login(...) {
```

### AuthController.java - DESPUÉS
```java
@PostMapping("/login")
@Operation(summary = "Login - genera JWT (PUBLIC)")
public ResponseEntity<GatewayResponse<LoginResponse>> login(...) {
```

---

## 🎯 CÓMO SE VE EN SWAGGER UI

Cuando abres `http://localhost:8081/swagger-ui.html`, ahora verás:

```
┌────────────────────────────────────────────────────────────┐
│ Auth                                                         │
├────────────────────────────────────────────────────────────┤
│ POST   /api/auth/login                                       │
│   Summary: Login - genera JWT (PUBLIC)                       │
│   Description: [default]                                     │
│   Security: [BearerAuth] or [no auth]                       │
│                                                              │
│ POST   /api/auth/register                                    │
│   Summary: Registrar nuevo usuario (PUBLIC)                 │
│   Description: [default]                                    │
│   Security: [no auth]                                       │
│                                                              │
│ GET    /api/auth/me                                         │
│   Summary: Obtener información del usuario actual            │
│           (AUTHENTICATED)                                   │
│   Description: [default]                                    │
│   Security: ✓ BearerAuth                                    │
│                                                              │
│ GET    /api/auth/{id}                                       │
│   Summary: Obtener usuario por ID (ADMIN ONLY)              │
│   Description: [default]                                    │
│   Security: ✓ BearerAuth (requiere rol ADMIN)              │
│                                                              │
│ GET    /api/auth/email/{email}                              │
│   Summary: Obtener usuario por email (ADMIN ONLY)           │
│   Description: [default]                                    │
│   Security: ✓ BearerAuth (requiere rol ADMIN)              │
└────────────────────────────────────────────────────────────┘
```

---

## 🏢 Resumen de cambios por controlador

### ✅ AuthController.java (5 endpoints)
- 2 públicos → `(PUBLIC)`
- 1 autenticado → `(AUTHENTICATED)`
- 2 admin → `(ADMIN ONLY)`

### ✅ CatalogController.java (8 endpoints)
- 6 públicos → `(PUBLIC)`
- 2 admin → `(ADMIN ONLY)`

### ✅ OrderController.java (4 endpoints)
- 4 affiliate → `(AFFILIATE ONLY)`

### ✅ LogisticsController.java (5 endpoints)
- 4 delivery → `(DELIVERY ONLY)`
- 1 admin → `(ADMIN ONLY)`

### ✅ AuctionController.java (8 endpoints)
- 2 admin → `(ADMIN ONLY)`
- 6 admin + affiliate → `(ADMIN + AFFILIATE)`

---

## 🔨 Controladores Modificados

1. ✅ `AuthController.java` - 5 anotaciones actualizadas
2. ✅ `CatalogController.java` - 8 anotaciones actualizadas
3. ✅ `OrderController.java` - 4 anotaciones actualizadas
4. ✅ `LogisticsController.java` - 5 anotaciones actualizadas
5. ✅ `AuctionController.java` - 8 anotaciones actualizadas

**Total: 30 endpoints con documentación OpenAPI completa**

---

## ✅ Validación

- ✅ Compilación: **SUCCESS**
- ✅ Syntax Checking: **PASS**
- ✅ No nuevos errores: **CONFIRMED**
- ✅ Anotaciones Swagger válidas: **CONFIRMED**

---

## 📚 CÓMO USAR

1. **Ver en Swagger UI**: 
   ```bash
   # Inicia la aplicación
   java -jar target/medigo-api-gateway-1.0.0.jar
   
   # Abre en navegador
   http://localhost:8081/swagger-ui.html
   ```

2. **Ver especificación OpenAPI JSON**:
   ```bash
   http://localhost:8081/v3/api-docs
   ```

3. **Ver especificación OpenAPI YAML**:
   ```bash
   http://localhost:8081/v3/api-docs.yaml
   ```

---

## 🎓 Ejemplo en Swagger UI

Cuando hagas clic en un endpoint en Swagger, ahora verás claramente:

```
POST /api/auth/login
  Summary: Login - genera JWT (PUBLIC)
  ⚠️ No requiere autenticación
  
Try it out →
  email: [                           ]
  password: [                        ]
  
  [Execute]
```

```
GET /api/auth/{id}
  Summary: Obtener usuario por ID (ADMIN ONLY)
  🔒 Requiere autenticación JWT con rol ADMIN
  Security: BearerAuth ✓
  
Try it out →
  id: [1]
  
  [Execute]
```

---

**Estado final**: ✅ Documentación OpenAPI completa con etiquetas de rol en todos los 30 endpoints

