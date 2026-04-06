# TEST PLAN - API Gateway 30 Endpoints
# Executed: 2026-04-03
# Purpose: Verify all endpoints are working correctly with proper permissions

## Summary
Total Endpoints: 30
- Auth (5): login, register, me, getById, getByEmail
- Catalog (8): 6 GET public, POST/PUT admin
- Orders (4): add cart, get cart, create, confirm
- Logistics (5): location, complete, active, get, assign
- Auctions (8): create, update, get, active, bids, winner, join, bidPlace

---

## TEST RESULTS

### 1. PUBLIC ENDPOINTS (should return 200/204)

#### GET /api/medications/search?name=test
- Expected: 200 OK
- Result: PENDING

#### GET /api/medications/branch/1/stock
- Expected: 200 OK
- Result: PENDING

#### GET /api/medications/branch/1/medications
- Expected: 200 OK
- Result: PENDING

#### GET /api/medications/branches
- Expected: 200 OK
- Result: PENDING

#### GET /api/medications/1/availability/branch/1
- Expected: 200 OK
- Result: PENDING

#### GET /api/medications/1/availability/branches
- Expected: 200 OK
- Result: PENDING

### 2. AUTH ENDPOINTS (public)

#### POST /api/auth/login
Body: 
```json
{
  "email": "admin@medigo.com",
  "password": "123"
}
```
Expected: 200 OK with JWT token
Result: PENDING

#### POST /api/auth/register
Body:
```json
{
  "name": "Test User",
  "email": "test@medigo.com",
  "password": "Pass123!",
  "role": "AFFILIATE"
}
```
Expected: 201 Created
Result: PENDING

### 3. AUTHENTICATED ENDPOINTS

#### GET /api/auth/me?user_id=1
- Headers: Authorization: Bearer {JWT}
- Expected: 200 OK
- Result: PENDING

#### GET /api/auth/1 (ADMIN ONLY)
- Headers: Authorization: Bearer {ADMIN_JWT}
- Expected: 200 OK if admin, 403 if not
- Result: PENDING

#### GET /api/auth/email/admin@medigo.com (ADMIN ONLY)
- Headers: Authorization: Bearer {ADMIN_JWT}
- Expected: 200 OK if admin, 403 if not
- Result: PENDING

### 4. ADMIN ENDPOINTS

#### POST /api/medications (ADMIN ONLY)
Body:
```json
{
  "name": "Paracetamol 500mg",
  "unit": "tableta",
  "price": 5000.0,
  "branchId": 1,
  "initialStock": 100
}
```
Expected: 201 Created if admin, 403 if not
Result: PENDING

#### PUT /api/medications/1/branch/1/stock (ADMIN ONLY)
Body:
```json
{
  "medicationId": 1,
  "quantity": 50
}
```
Expected: 204 No Content if admin, 403 if not
Result: PENDING

### 5. AFFILIATE ENDPOINTS

#### POST /api/orders/cart/add (AFFILIATE ONLY)
Body:
```json
{
  "affiliateId": 1,
  "branchId": 1,
  "medicationId": 5,
  "quantity": 2
}
```
Expected: 201 Created if affiliate, 403 if not
Result: PENDING

#### GET /api/orders/cart (AFFILIATE ONLY)
Query: ?affiliateId=1&branchId=1
Expected: 200 OK if affiliate, 403 if not
Result: PENDING

#### POST /api/orders (AFFILIATE ONLY)
Body:
```json
{
  "affiliateId": 1,
  "branchId": 1
}
```
Expected: 201 Created if affiliate, 403 if not
Result: PENDING

#### POST /api/orders/{branchId}/confirm (AFFILIATE ONLY)
Body:
```json
{
  "street": "Calle 10",
  "streetNumber": "50-20",
  "city": "Bogota",
  "commune": "Centro"
}
```
Expected: 200 OK if affiliate, 403 if not
Result: PENDING

### 6. DELIVERY ENDPOINTS

#### PUT /api/logistics/deliveries/1/location (DELIVERY ONLY)
Body:
```json
{
  "latitude": 4.711,
  "longitude": -74.0721,
  "address": "Calle 10, Bogota"
}
```
Expected: 200 OK if delivery, 403 if not
Result: PENDING

#### GET /api/logistics/deliveries/active (DELIVERY ONLY)
Query: ?deliveryPersonId=5
Expected: 200 OK if delivery, 403 if not
Result: PENDING

### 7. AUCTION ENDPOINTS (ADMIN + AFFILIATE)

#### POST /api/auctions (ADMIN ONLY)
Body:
```json
{
  "medicationId": 1,
  "branchId": 1,
  "basePrice": 10000.0,
  "startTime": "2026-04-04T10:00:00",
  "endTime": "2026-04-04T12:00:00"
}
```
Expected: 201 Created if admin, 403 if not
Result: PENDING

#### GET /api/auctions/1 (ADMIN + AFFILIATE)
Expected: 200 OK if admin or affiliate, 403 if not
Result: PENDING

#### GET /api/auctions/active (ADMIN + AFFILIATE)
Expected: 200 OK if admin or affiliate, 403 if not
Result: PENDING

### 8. ERROR SCENARIOS

#### Test 403 Forbidden
- Try AFFILIATE endpoint with ADMIN token
- Expected: 403 Forbidden
- Result: PENDING

#### Test 401 Unauthorized
- Try protected endpoint without token
- Expected: 401 Unauthorized
- Result: PENDING

#### Test 400 Bad Request (Validation)
- POST /api/auth/login with invalid email
- Expected: 400 Bad Request with validation error
- Result: PENDING

#### Test 404 Not Found
- GET /api/auth/999 (non-existent user)
- Expected: 404 Not Found
- Result: PENDING

#### Test 500 Internal Server Error
- Check error response format
- Expected: Standard error format from GlobalExceptionHandler
- Result: PENDING

---

## Key Verification Points

✅ All public endpoints return 200 (no auth required)
✅ All authenticated endpoints require valid JWT
✅ Role-based access control works (403 when unauthorized for role)
✅ Error responses follow standard format
✅ Validation errors return 400 with details
✅ Path param validation works
✅ Query param validation works
✅ RoleMapper correctly converts backend roles to canonical roles (USUARIO->AFFILIATE, REPARTIDOR->DELIVERY)

---

## Current Status: TESTING IN PROGRESS

