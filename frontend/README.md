# 🚀 E-Commerce Vue.js + Vite - Guía de Instalación

## 📋 Estructura Creada

```
frontend/
├── index.html
├── package.json
├── vite.config.js
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── config/
│   │   └── axios.js
│   ├── router/
│   │   └── index.js
│   ├── stores/
│   │   ├── auth.js
│   │   └── cart.js
│   ├── components/
│   │   ├── Navbar.vue
│   │   └── Footer.vue
│   └── views/
│       ├── Home.vue
│       ├── Catalog.vue
│       ├── ProductDetail.vue (crear)
│       ├── Cart.vue (crear)
│       ├── Checkout.vue (crear)
│       ├── Confirmation.vue (crear)
│       ├── Login.vue (crear)
│       ├── Register.vue (crear)
│       ├── Orders.vue (crear)
│       ├── UserProfile.vue (crear)
│       └── admin/
│           ├── AdminDashboard.vue (crear)
│           ├── AdminProducts.vue (crear)
│           ├── AdminUsers.vue (crear)
│           ├── ProductForm.vue (crear)
│           └── UserForm.vue (crear)
```

## 🎯 Paso 1: Instalar Dependencias

Abre PowerShell y navega a la carpeta frontend:

```powershell
cd C:\Users\LENOVO\Documents\utp\ciclo7\marcos_desarrollo_web\ecommerce-thymeleaf\frontend
npm install
```

Esto instalará:
- Vue 3
- Vue Router
- Pinia (state management)
- Axios
- Bootstrap 5
- Vite

## 🎯 Paso 2: Iniciar el Servidor de Desarrollo

```powershell
npm run dev
```

El frontend estará disponible en: `http://localhost:3000`

## 🎯 Paso 3: Configurar el Backend (Spring Boot)

### 3.1. Agregar CORS Configuration

Crea el archivo `WebConfig.java`:

```java
package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 3.2. Crear Controladores REST

Necesitas crear controladores REST que devuelvan JSON en lugar de vistas Thymeleaf:

#### ProductRestController.java
```java
@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public List<Product> getAllProducts(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return productRepository.findByNombreContainingIgnoreCase(search);
        }
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
```

#### CartRestController.java
```java
@RestController
@RequestMapping("/api/cart")
public class CartRestController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public ResponseEntity<Cart> getCart(HttpSession session) {
        Cart cart = cartService.getCart(session);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/add/{productId}")
    public ResponseEntity<Void> addToCart(@PathVariable Long productId, 
                                          @RequestParam(defaultValue = "1") int qty,
                                          HttpSession session) {
        cartService.addItem(session, productId, qty);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/update/{productId}")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long productId,
                                               @RequestParam int qty,
                                               HttpSession session) {
        cartService.updateQuantity(session, productId, qty);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/remove/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId,
                                           HttpSession session) {
        cartService.removeItem(session, productId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok().build();
    }
}
```

## 🎯 Paso 4: Copiar Imágenes

Copia las imágenes de `src/main/resources/static/img/` a `frontend/public/img/`:

```powershell
# Crear carpeta
New-Item -ItemType Directory -Force -Path frontend/public/img

# Copiar imágenes
Copy-Item -Path "src/main/resources/static/img/*" -Destination "frontend/public/img/" -Recurse
```

## 🎯 Paso 5: Ejecutar Ambos Servidores

### Terminal 1: Backend (Spring Boot)
```powershell
mvn spring-boot:run
```
Correrá en: `http://localhost:8080`

### Terminal 2: Frontend (Vue + Vite)
```powershell
cd frontend
npm run dev
```
Correrá en: `http://localhost:3000`

## 🔄 Flujo de Trabajo

1. **Frontend (Vue)** se ejecuta en `localhost:3000`
2. **Backend (Spring Boot)** se ejecuta en `localhost:8080`
3. **Vite Proxy** redirige `/api/*` al backend automáticamente
4. **CORS** permite la comunicación entre ambos

## 📝 Vistas Faltantes por Crear

Aún faltan por crear estas vistas (puedo crearlas si lo necesitas):

1. ✅ Home.vue (Creada)
2. ✅ Catalog.vue (Creada)
3. ⏳ ProductDetail.vue
4. ⏳ Cart.vue
5. ⏳ Checkout.vue
6. ⏳ Confirmation.vue
7. ⏳ Login.vue
8. ⏳ Register.vue
9. ⏳ Orders.vue
10. ⏳ UserProfile.vue
11. ⏳ AdminDashboard.vue
12. ⏳ AdminProducts.vue
13. ⏳ AdminUsers.vue
14. ⏳ ProductForm.vue
15. ⏳ UserForm.vue

## 🚀 Próximos Pasos

1. **Instala las dependencias:** `cd frontend && npm install`
2. **Inicia el frontend:** `npm run dev`
3. **Configura CORS en el backend**
4. **Crea los controladores REST**
5. **Prueba la integración**

## 📌 Notas Importantes

- El frontend es **SPA (Single Page Application)**
- **No recargas la página** al navegar (Vue Router)
- **API REST** para comunicación con el backend
- **JWT** o **Session** para autenticación
- **Bootstrap 5** para estilos

## 🛠️ Comandos Útiles

```powershell
# Instalar dependencias
npm install

# Desarrollo
npm run dev

# Build para producción
npm run build

# Preview build
npm run preview
```

## ✅ Ventajas de Vue + Vite

1. ✅ **Más rápido:** Hot Module Replacement instantáneo
2. ✅ **SPA moderna:** Sin recargas de página
3. ✅ **Componentizable:** Reutiliza componentes
4. ✅ **Reactive:** Actualización automática del DOM
5. ✅ **TypeScript ready:** Si quieres usar TypeScript
6. ✅ **Build optimizado:** Bundle pequeño para producción

---

**¿Quieres que continúe creando las vistas restantes?** Puedo crear todas las vistas que faltan (Cart, Login, Register, etc.) o puedo crear solo las que necesites primero. 🚀

