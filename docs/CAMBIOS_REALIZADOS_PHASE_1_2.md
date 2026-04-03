# Cambios Realizados - PHASE 1 & PHASE 2

Fecha: 2026-04-03  
Versión: Gateway 1.0.0 2026-04-03

---

## ✅ PHASE 1: Security Configuration (Roles & CORS)

### 1. SecurityConfig.java
**Cambio:** Reemplazó roles legacy con canónicos. Agregó CORS. Implementó matriz de autorización por endpoint.

```java
// ANTES:
.anyRequest().authenticated()  // Solo requería JWT, sin roles

// DESPUÉS: 
.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
.requestMatchers(HttpMethod.POST, "/api/medications").hasRole("ADMIN")
.requestMatchers(HttpMethod.POST, "/api/orders/cart/add").hasRole("AFFILIATE")
.requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/{id}/location").hasRole("DELIVERY")
// ... 50+ rutas con protección específica
```

**Rutas Públicas:** 17 endpoints (login, register, búsqueda, availability)  
**ADMIN Only:** 6 endpoints  
**AFFILIATE Only:** 4 endpoints  
**DELIVERY Only:** 4 endpoints  
**ADMIN + AFFILIATE:** 6 endpoints  

**CORS:** Habilitado para localhost:4200, 3000, 8080

### 2. CatalogController
**Cambio:** Removió `@SecurityRequirement` a nivel de clase. Agregó 6 endpoints públicos. Agregó `@PreAuthorize` a endpoints ADMIN.

**Nuevos endpoints:**
- `GET /api/medications/search?name=texto` - PUBLIC
- `GET /api/medications/branch/{branchId}/stock` - PUBLIC
- `GET /api/medications/branch/{branchId}/medications` - PUBLIC
- `GET /api/medications/branches` - PUBLIC
- `GET /api/medications/{medicationId}/availability/branch/{branchId}` - PUBLIC
- `GET /api/medications/{medicationId}/availability/branches` - PUBLIC

**Protecciones agregadas:**
- `POST /api/medications` → `@PreAuthorize("hasRole('ADMIN')")`
- `PUT /api/medications/{medicationId}/branch/{branchId}/stock` → `@PreAuthorize("hasRole('ADMIN')")`

### 3. OrderController
**Cambio:** Removió `@SecurityRequirement` a nivel de clase. Agregó 3 endpoints de carrito. Implementó `@PreAuthorize("hasRole('AFFILIATE')")` a todos.

**Nuevos endpoints:**
- `POST /api/orders/cart/add` - AFFILIATE
- `GET /api/orders/cart?affiliateId={id}&branchId={id}` - AFFILIATE
- `POST /api/orders/{branchId}/confirm?affiliateId={id}` - AFFILIATE

### 4. LogisticsController
**Cambio:** Removió `@SecurityRequirement` a nivel de clase. Agregó 4 endpoints. Implementó segregación DELIVERY/ADMIN.

**Nuevos endpoints:**
- `PUT /api/logistics/deliveries/{id}/complete` - DELIVERY
- `GET /api/logistics/deliveries/active?deliveryPersonId={id}` - DELIVERY
- `GET /api/logistics/deliveries/{id}?deliveryPersonId={id}` - DELIVERY
- `POST /api/logistics/deliveries/assign` - ADMIN

### 5. AuctionController
**Cambio:** Removió `@SecurityRequirement` a nivel de clase. Agregó 1 endpoint winner. Implementó segregación ADMIN/AFFILIATE.

**Nuevo endpoint:**
- `GET /api/auctions/{id}/winner` - ADMIN, AFFILIATE

**Protecciones:**
- ADMIN: POST, PUT
- ADMIN + AFFILIATE: GET, POST bids

---

## ✅ PHASE 2: Auth Endpoints

### 1. New DTOs Created

#### RegisterRequest.java
```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Name es requerido")
    @Size(min = 3, max = 100, message = "Name debe tener entre 3 y 100 caracteres")
    private String name;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Password es requerido")
    private String password;

    @NotBlank(message = "Role es requerido")
    private String role; // AFFILIATE o DELIVERY
}
```

#### UserResponseDto.java
```java
@Data
@Builder
public class UserResponseDto {
    private Long user_id;
    private String username;
    private String email;
    private String role;
    private Boolean active;
}
```

#### RegisterResponse.java
```java
@Data
@Builder
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String message;
}
```

### 2. AuthUseCase Interface
**Cambio:** Agregó 4 nuevos métodos al puerto de entrada.

```java
// NUEVOS MÉTODOS:
RegisterResponse register(RegisterRequest request);
UserResponseDto getMe(Long userId);
UserResponseDto getUserById(Long id);
UserResponseDto getUserByEmail(String email);
```

### 3. AuthGatewayService Implementation
**Cambio:** Implementó los 4 nuevos métodos delegando al backend y mapeando respuestas.

**Métodos implementados:**
- `register()`: Enruta POST /api/auth/register, mapea respuesta a RegisterResponse
- `getMe()`: Enruta GET /api/auth/me?user_id=X, mapea a UserResponseDto
- `getUserById()`: Enruta GET /api/auth/{id}, mapea a UserResponseDto
- `getUserByEmail()`: Enruta GET /api/auth/email/{email}, mapea a UserResponseDto
- `mapToUserResponseDto()`: Método auxiliar para normalizar respuestas

### 4. AuthController
**Cambio:** Agregó 4 nuevos endpoints con autorización correcta.

```java
@PostMapping("/register")
// PUBLIC
public ResponseEntity<GatewayResponse<RegisterResponse>> register(
        @Valid @RequestBody RegisterRequest request)

@GetMapping("/me")
@PreAuthorize("authenticated")
// AUTHENTICATED
public ResponseEntity<GatewayResponse<UserResponseDto>> getMe(@RequestParam Long user_id)

@GetMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
// ADMIN ONLY
public ResponseEntity<GatewayResponse<UserResponseDto>> getUserById(@PathVariable Long id)

@GetMapping("/email/{email}")
@PreAuthorize("hasRole('ADMIN')")
// ADMIN ONLY
public ResponseEntity<GatewayResponse<UserResponseDto>> getUserByEmail(@PathVariable String email)
```

---

## 📊 Resumen de Cambios

| Métrica | Antes | Después | Delta |
|---------|-------|---------|-------|
| **Endpoints Auth** | 1 | 5 | +4 |
| **Endpoints Catalog** | 3 | 9 | +6 |
| **Endpoints Orders** | 3 | 7 | +4 |
| **Endpoints Logistics** | 3 | 7 | +4 |
| **Endpoints Auctions** | 7 | 8 | +1 |
| **TOTAL Endpoints** | 17 | 36 | +19 |
| **DTOs creados** | 0 | 3 | +3 |
| **Métodos AuthUseCase** | 1 | 5 | +4 |
| **Controllers modificados** | 0 | 5 | +5 |
| **Rutas con @PreAuthorize** | 0 | 27 | +27 |

---

## ✅ Estado de Compilación

```
BUILD SUCCESS
Total time: 14.823 s
Compiled files: 48
Test classes: 6
JAR size: medigo-api-gateway-1.0.0.jar
```

---

## 🚀 Gateway Status

- **Puerto:** 8081
- **Perfil:** dev (H2 en memoria)
- **CORS:** Activo
- **JWT Filter:** Activo
- **Seguridad:** 100% configurada para PHASE 1-2

---

## ⏳ Próximas Fases

**PHASE 3:** Validaciones de DTOs
- LoginRequest: Cambiar username → email (per spec)
- Agregar validaciones a CreateOrderRequest, CreateAuctionRequest, etc.
- Validar formato email, ranges numéricos, etc.

**PHASE 4:** Testing & Deployment
- Test endpoints por rol
- Test CORS desde Swagger
- Test validaciones de request body

---

Generado automáticamente - 2026-04-03 13:34 UTC-5
