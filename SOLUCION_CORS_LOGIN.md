# Solución CORS y Login REST - Problema HTML en respuesta API

## ⚠️ PROBLEMA IDENTIFICADO

**Síntoma:** Al hacer login desde Vue, el backend devuelve **HTML** (página de login de Thymeleaf) en lugar de JSON.

**Causa raíz:** Spring Security estaba interceptando las peticiones a `/api/auth/login` y redirigiendo al formulario de login tradicional (`/login`) porque:
1. Solo se permitía explícitamente `/api/auth/login` y `/api/auth/register`
2. El endpoint `/api/auth/me` y `/api/auth/logout` NO estaban permitidos
3. Spring Security aplicaba `formLogin()` a TODAS las rutas, incluyendo las API REST

**Solución aplicada:**
- Cambiado `.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()` 
- Por: `.requestMatchers("/api/auth/**").permitAll()` (permite TODOS los endpoints de autenticación)
- Esto asegura que `/api/auth/login`, `/api/auth/register`, `/api/auth/me` y `/api/auth/logout` respondan JSON correctamente

---

## Qué se corrigió

### 1. Configuración CORS Global (nuevo archivo)
**Archivo:** `src/main/java/com/example/ecommerce/config/CorsConfig.java`

Se creó una configuración CORS global que:
- Permite orígenes: `http://localhost:3000` y `http://localhost:5173`
- Permite métodos: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Permite credenciales (cookies, sesiones)
- Aplica a todos los endpoints `/api/**`

### 2. WebSecurityConfig actualizado
**Archivo:** `src/main/java/com/example/ecommerce/config/WebSecurityConfig.java`

Se añadió:
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource))
```

Y se cambió:
```java
// ANTES (solo permitía login y register)
.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

// AHORA (permite todos los endpoints de auth)
.requestMatchers("/api/auth/**").permitAll()
```

**Esto es CRÍTICO:** ahora `/api/auth/me` y `/api/auth/logout` responden JSON en lugar de redirigir al formulario HTML.

### 3. Login.vue corregido
**Archivo:** `frontend/src/views/Login.vue`

Se corrigió el manejo del resultado de `authStore.login()` para verificar correctamente `result.success`.

### 4. main.js actualizado
**Archivo:** `frontend/src/main.js`

Se añadió `authStore.checkAuth()` antes de montar la app para restaurar sesión desde localStorage.

### 5. AuthRestController actualizado
**Archivo:** `src/main/java/com/example/ecommerce/controller/AuthRestController.java`

Se actualizó `@CrossOrigin` para incluir ambos puertos (`3000` y `5173`).

### 6. Página de prueba TestAuth.vue
**Archivo:** `frontend/src/views/TestAuth.vue`

Nueva página para probar login, `/api/auth/me`, `/api/users` y logout.
Accesible en: `http://localhost:3000/test-auth` (o `:5173/test-auth`)

---

## Cómo probar

### Paso 1: Recompilar backend
Abre PowerShell en la raíz del proyecto:

```powershell
mvn clean compile
```

### Paso 2: Reiniciar backend
```powershell
mvn spring-boot:run
```

O usa el script:
```powershell
.\start-dev.bat
```

### Paso 3: Reiniciar frontend
Abre otra terminal PowerShell:

```powershell
cd frontend
npm run dev
```

### Paso 4: Probar en el navegador

1. Abre: `http://localhost:3000/test-auth` (o el puerto que use Vite, usualmente 5173)

2. **Prueba de Login:**
   - Email: `admin@example.com` (según los logs del backend)
   - Password: la que hayas configurado (probablemente `admin123` o `password`)
   - Click en **Login**
   - **IMPORTANTE:** Ahora deberías ver JSON en lugar de HTML:
     ```json
     Status: 200
     Body: {
       "message": "Login exitoso",
       "user": { 
         "correo": "admin@example.com",
         "nombre": "Admin",
         "usuario": "admin",
         "roles": "USER,ADMIN"
       },
       "token": "..."
     }
     ```
   - **SI SIGUE DEVOLVIENDO HTML:** El backend no se reinició correctamente. Detén el proceso (Ctrl+C) y ejecuta `mvn spring-boot:run` de nuevo.

3. **Prueba GET /api/auth/me:**
   - Click en **GET /api/auth/me**
   - Deberías ver status 200 con datos del usuario
   - Si devuelve 401, la sesión no se guardó (revisar cookies en DevTools)

4. **Prueba GET /api/users (solo ADMIN):**
   - Click en **GET /api/users**
   - Si el usuario tiene rol ADMIN: status 200 con lista de usuarios
   - Si no es ADMIN: status 403

5. **Verifica en DevTools:**
   - Pestaña **Network**: la petición a `/api/auth/login` debe mostrar:
     - Response Headers: `Set-Cookie: JSESSIONID=...`
     - Response Headers: `Access-Control-Allow-Origin: http://localhost:3000`
     - Response Headers: `Access-Control-Allow-Credentials: true`
   - Pestaña **Application** → **Cookies** → `http://localhost:8080`:
     - Debe aparecer cookie `JSESSIONID`
   - Pestaña **Application** → **Local Storage** → `http://localhost:3000`:
     - Debe tener `user` y `token`

---

## Qué esperar

### ✅ Si funciona correctamente:
- Login devuelve status 200
- Cookie `JSESSIONID` aparece en Application → Cookies
- `/api/auth/me` devuelve status 200 con datos del usuario
- `/api/users` devuelve 200 (si eres ADMIN) o 403 (si eres USER)
- **NO hay errores CORS en consola**

### ❌ Si sigue fallando:

#### Caso 1: Sigue error CORS
**Síntoma:** "Access to XMLHttpRequest ... has been blocked by CORS policy"

**Solución:**
1. Verifica que recompilaste y reiniciaste el backend (`mvn clean compile`, `mvn spring-boot:run`)
2. Confirma en logs del backend al arrancar que Spring carga `CorsConfig`:
   - Busca en logs: "Bean 'corsConfigurationSource' of type [CorsConfigurationSource]"
3. Si usas otro puerto (no 3000 ni 5173), agrégalo en `CorsConfig.java`:
   ```java
   configuration.setAllowedOrigins(Arrays.asList(
       "http://localhost:3000",
       "http://localhost:5173",
       "http://localhost:TU_PUERTO"  // <-- añadir
   ));
   ```

#### Caso 2: Login devuelve 401
**Síntoma:** Response body: `{ "error": "Credenciales incorrectas" }`

**Solución:**
- Las credenciales son incorrectas o el usuario no existe
- Revisa logs del backend para ver queries de Hibernate
- Verifica en MySQL:
  ```sql
  USE sistema_pedidos;
  SELECT correo, nombre, roles FROM usuarios;
  ```
- La contraseña debe estar encriptada en la DB (comienza con `{bcrypt}` o similar)

#### Caso 3: Cookie no se guarda
**Síntoma:** `/api/auth/me` devuelve 401 aunque login fue 200

**Solución:**
- Verifica en Network → Response Headers del login que aparece `Set-Cookie: JSESSIONID=...`
- Si no aparece:
  - Problema con `allowCredentials` o dominio/puerto inconsistente
  - Asegúrate de que el frontend está usando exactamente `http://localhost:3000` (no `127.0.0.1`)
- Si aparece pero no se guarda:
  - Verifica que axios tiene `withCredentials: true` (ya está en `config/axios.js`)
  - El navegador puede bloquear cookies de terceros; verifica configuración de cookies del navegador

#### Caso 4: `/api/users` devuelve 403 (Forbidden)
**Síntoma:** Usuario logueado pero no puede acceder a `/api/users`

**Solución:**
- El endpoint `/api/users` requiere rol `ADMIN`
- Verifica el rol del usuario:
  ```sql
  SELECT correo, roles FROM usuarios WHERE correo = 'tu_email@test.com';
  ```
- Si el usuario es `USER`, cambia a `ADMIN`:
  ```sql
  UPDATE usuarios SET roles = 'ADMIN' WHERE correo = 'tu_email@test.com';
  ```

---

## Archivos modificados/creados

### Creados:
1. `src/main/java/com/example/ecommerce/config/CorsConfig.java` (CORS global)
2. `frontend/src/views/TestAuth.vue` (página de prueba)

### Modificados:
1. `src/main/java/com/example/ecommerce/config/WebSecurityConfig.java` (habilitar CORS)
2. `src/main/java/com/example/ecommerce/controller/AuthRestController.java` (permitir :5173)
3. `frontend/src/views/Login.vue` (corregir lógica de respuesta)
4. `frontend/src/main.js` (añadir checkAuth al inicio)
5. `frontend/src/router/index.js` (añadir ruta /test-auth)

---

## Comandos rápidos

```powershell
# Backend
cd C:\Users\LENOVO\Documents\utp\ciclo7\marcos_desarrollo_web\ecommerce-thymeleaf
mvn clean compile
mvn spring-boot:run

# Frontend (en otra terminal)
cd C:\Users\LENOVO\Documents\utp\ciclo7\marcos_desarrollo_web\ecommerce-thymeleaf\frontend
npm run dev
```

Luego abre: `http://localhost:3000/test-auth` (o el puerto que muestre Vite)

---

## Credenciales de prueba

Según los logs del backend que proporcionaste, los usuarios creados son:

**Usuario normal:**
- Email: `user@example.com`
- Rol: `USER`

**Usuario administrador:**
- Email: `admin@example.com`
- Rol: `USER,ADMIN`

**Contraseña:** Revisa el archivo `EcommerceApplication.java` para confirmar la contraseña configurada (probablemente `password` o `admin123`).

