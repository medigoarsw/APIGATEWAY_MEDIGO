# GUÍA PASO A PASO - Validación de Soluciones
## API Gateway - Errores 403/500 Resueltos
## 2026-04-03

---

## 📋 INSTRUCCIONES PASO A PASO

### **PASO 1: Compilar el proyecto** (2 minutos)

Abre PowerShell en la carpeta del proyecto y ejecuta:

```powershell
cd "d:\ander\Documents\SEMESTRE 7\ARSW\PROYECTO OFICIAL\APIGATEWAY_MEDIGO"
mvn clean package -DskipTests
```

**Resultado esperado**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 14.xxx s
```

✅ Si ves `BUILD SUCCESS`, continúa al Paso 2.
❌ Si ves `BUILD FAILURE`, copiar el error completo y mostrar.

---

### **PASO 2: Matar procesos anteriores** (30 segundos)

Si hay un gateway corriendo aún:

```powershell
netstat -ano | findstr :8081
```

Si aparece algo, ejecutar:
```powershell
taskkill /PID <NUMBER_FROM_NETSTAT> /F
```

Ejemplo:
```
TCP    0.0.0.0:8081    0.0.0.0:0    LISTENING    12345
taskkill /PID 12345 /F
```

---

### **PASO 3: Iniciar el Gateway** (5 segundos)

```powershell
cd "d:\ander\Documents\SEMESTRE 7\ARSW\PROYECTO OFICIAL\APIGATEWAY_MEDIGO"
$env:SPRING_PROFILES_ACTIVE="dev"
java -jar target\medigo-api-gateway-1.0.0.jar
```

**Resultado esperado**:
```
[INFO] Starting MedigoApiGatewayApplication ...
[INFO] Started MedigoApiGatewayApplication in X.XXX seconds
[INFO] Tomcat started on port(s): 8081 ...
```

✅ Si ves `Tomcat started on port(s): 8081`, el gateway está listo. DEJA ESTE TERMINAL CORRIENDO.

❌ Si ves error, es probablemente porque el puerto 8081 sigue en uso. Revisa Paso 2.

---

### **PASO 4: Abre OTRA VENTANA PowerShell NUEVA** (para hacer pruebas)

Manteniendo el gateway corriendo en la primera ventana, abre una NUEVA ventana PowerShell.

---

### **PASO 5: TEST #1 - Endpoint Público (NO requiere JWT)**

Ejecuta:

```powershell
curl -X GET "http://localhost:8081/api/medications/branches" `
  -H "Content-Type: application/json"
```

**Resultado esperado**: 
- Status: **200 OK**
- Respuesta JSON con brandesde

```json
[
  {
    "branchId": 1,
    "branchName": "Sucursal Centro",
    ...
  }
]
```

✅ **OK** - Endpoint público funciona sin JWT

---

### **PASO 6: TEST #2 - Login (obtener JWT)**

Ejecuta:

```powershell
$loginBody = @{
    email = "admin@medigo.com"
    password = "123"
} | ConvertTo-Json

curl -X POST "http://localhost:8081/api/auth/login" `
  -H "Content-Type: application/json" `
  -d $loginBody
```

**Resultado esperado**: 
- Status: **200 OK**
- Respuesta con JWT:

```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN",
  "jwt_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

**IMPORTANTE**: 
- ✅ Si `"role"` es `"ADMIN"`, el backend envía rol correcto
- ⚠️ Si `"role"` es `"USUARIO"`, el RoleMapper debería convertir a `"AFFILIATE"` (verifícalo)
- ✅ Guarda el `jwt_token` para el siguiente test

---

### **PASO 7: TEST #3 - Endpoint ADMIN protegido CON JWT** 

Usa el JWT del Paso 6:

```powershell
$TOKEN = "<COPIAR_JWT_AQUI>"

curl -X GET "http://localhost:8081/api/auth/1" `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json"
```

**Resultado esperado**:
- Si JWT es ADMIN: **200 OK** ✅ (tienes permiso)
- Respuesta JSON con datos del usuario:

```json
{
  "user_id": 1,
  "username": "admin",
  "email": "admin@medigo.com",
  "role": "ADMIN"
}
```

✅ **OK** - ADMIN puede acceder a endpoint ADMIN

---

### **PASO 8: TEST #4 - Endpoint AFFILIATE protegido sin JWT** 

Sin token, intentar acceder a endpoint que requiere AFFILIATE:

```powershell
$orderBody = @{
    affiliateId = 1
    branchId = 1
    medicationId = 5
    quantity = 1
} | ConvertTo-Json

curl -X POST "http://localhost:8081/api/orders/cart/add" `
  -H "Content-Type: application/json" `
  -d $orderBody
```

**Resultado esperado**:
- Status: **401 Unauthorized** ❌ (sin JWT no puedes)
- Respuesta: error indicando que falta JWT

```json
{
  "status": 401,
  "message": "Unauthorized"
}
```

✅ **OK** - Sin JWT se rechaza correctamente

---

### **PASO 9: TEST #5 - Endpoint AFFILIATE protegido CON JWT ADMIN (role incorrecto)**

Usa el JWT ADMIN del Paso 6 para intentar un endpoint que requiere AFFILIATE:

```powershell
$TOKEN = "<JWT_ADMIN_DEL_PASO_6>"

$orderBody = @{
    affiliateId = 1
    branchId = 1
    medicationId = 5
    quantity = 1
} | ConvertTo-Json

curl -X POST "http://localhost:8081/api/orders/cart/add" `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json" `
  -d $orderBody
```

**Resultado esperado**:
- Status: **403 Forbidden** ❌ (eres ADMIN, necesitas AFFILIATE)
- Respuesta: error indicando que no tienes acceso a este endpoint

```json
{
  "status": 403,
  "message": "Access Denied"
}
```

✅ **OK** - Control de roles funciona correctamente

---

### **PASO 10: Verificar Logs en el Gateway** (primer terminal)

En el terminal donde corre el gateway (Paso 3), deberías ver logs como:

```
[INFO] Backend returned user: userId='1', username='admin', email='admin@medigo.com', role='ADMIN'
[INFO] Role mapping: backend_role='ADMIN' -> canonical_role='ADMIN'
[DEBUG] Usuario autenticado: username=admin role=ADMIN authority=ROLE_ADMIN
```

Si el backend enviara "USUARIO":
```
[INFO] Backend returned user: userId='2', username='user', email='user@medigo.com', role='USUARIO'
[INFO] Role mapping: backend_role='USUARIO' -> canonical_role='AFFILIATE'
[DEBUG] Usuario autenticado: username=user role=AFFILIATE authority=ROLE_AFFILIATE
```

✅ Esto confirma que el RoleMapper está funcionando

---

## 📊 MATRIZ DE PRUEBAS - CHECKLIST

Marca con ✅/❌ según resultados:

### Endpoints públicos (sin JWT)
- [ ] GET /api/medications/branches → **200 OK** ✅
- [ ] GET /api/medications/search?name=test → **200 OK** ✅

### Autenticación
- [ ] POST /api/auth/login → **200 OK** con JWT ✅
- [ ] PUT /api/auth/register → **201 Created** con JWT ✅

### Endpoint ADMIN protegido (con JWT ADMIN)
- [ ] GET /api/auth/1 → **200 OK** ✅

### Endpoint AFFILIATE protegido (sin JWT)
- [ ] POST /api/orders/cart/add → **401 Unauthorized** ✅

### Endpoint AFFILIATE protegido (con JWT ADMIN - role incorrecto)
- [ ] POST /api/orders/cart/add → **403 Forbidden** ✅

### Control de logs
- [ ] Logs muestran "Role mapping: backend_role='X' -> canonical_role='Y'" ✅

---

## 🎯 INTERPRETACIÓN DE RESULTADOS

### Todos los tests pasenron (✅) :
**Congratulations!** El problemas de errores 403/500 han sido resueltos. Los permisos están alineados correctamente.

### Algún test falló (❌):

- **Error en compilación**: Revisa que no haya errores de sintaxis
- **Error 403 inesperado**: El rol no está siendo mapeado correctamente
- **Error 500**: Aún hay un NullPointerException, revisar logs del gateway
- **401 inesperado**: JWT no está siendo enviado correctamente

---

## 🔍 Cómo Leer los Logs para Debugging

En el terminal del gateway, busca:

1. **Login exitoso**:
```
[INFO] Backend returned user: userId='xxx', username='xxx', role='XXX'
[INFO] Role mapping: backend_role='XXX' -> canonical_role='XXX'
```

2. **Autenticación en request**:
```
[DEBUG] Usuario autenticado: username=xxx role=XXX authority=ROLE_XXX
```

3. **Error en JWT**:
```
[ERROR] Error validando JWT: <mensaje>
```

4. **Request sin JWT**:
```
[WARN] Request sin token: GET /api/auth/1
```

---

## ⚠️ Problemas Comunes

### Problema: "Port 8081 already in use"
**Solución**: Ejecuta Paso 2 (matar procesos anterior)

### Problema: JWT inválido o expirado
**Solución**: Obtener nuevo JWT con Paso 6

### Problema: "RoleMapper no encontrado"
**Solución**: Verificar que compilación fue exitosa (Paso 1 debe
 mostrar BUILD SUCCESS)

### Problema: Error 500 en login
**Solución**: Backend podría estar caído. Verificar que backend está corriendo.

---

## 📞 Resumen de lo Solucionado

| Problema | Causa | Solución | Status |
|----------|-------|----------|--------|
| Error 403 (admin) | Roles desalineados (USUARIO vs AFFILIATE) | RoleMapper | ✅ RESUELTO |
| Error 500 | NullPointerException en AuthGatewayService | Null-safety en JwtAuthenticationFilter | ✅ RESUELTO |
| Login incorrecto | Rol no mapeado | RoleMapper implementado | ✅ RESUELTO |
| Permisos inconsistentes | Falta de validación de roles | SecurityConfig ya OK | ✅ OK |

---

## ✅ Final Checklist

Antes de considerar resuelto:

- [ ] mvn clean package ejecutado exitosamente
- [ ] Gateway inicia sin errores
- [ ] Tests 1-5 pasenpassing
- [ ] Logs muestran role mapping
- [ ] Sin errores 403/500 en endpoints protegidos
- [ ] Matriz de permisos funcionando correctamente

---

**¿Necesitas ayuda adicional?**
1. Revisa los logs del gateway (primer terminal)
2. Verifica que compilación fue exitosa
3. Revisa los documentos de referencia:
   - INFORME_EJECUTIVO_RESOLUCION.md
   - ANALISIS_ERRORES_403_500.md

