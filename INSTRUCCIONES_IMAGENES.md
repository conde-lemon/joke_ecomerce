# 🎨 INSTRUCCIONES PARA COMPLETAR LA CONFIGURACIÓN DE IMÁGENES

## ✅ Cambios Realizados

### Frontend:
1. ✅ **Home.vue** - Banner actualizado con `banner.jpg`
2. ✅ **ProductForm.vue** - Formulario completo para crear/editar productos con selector de imágenes
3. ✅ **AdminProducts.vue** - Ya mostraba miniaturas de productos

### Backend:
1. ✅ **Product.java** - Ya tenía el campo `imageUrl`
2. ✅ **ProductRestController.java** - Ya maneja `imageUrl` en CRUD, actualizado CORS
3. ✅ **EcommerceApplication.java** - Productos de prueba actualizados con rutas `/img/pX.jpg`

---

## 📋 ACCIÓN REQUERIDA: Copiar Imágenes al Backend

Las imágenes están actualmente en:
```
frontend/src/img/
```

Necesitan copiarse a:
```
src/main/resources/static/img/
```

### Opción 1: Copiar manualmente (PowerShell)

```powershell
# Crear carpeta img en backend si no existe
New-Item -Path "src\main\resources\static\img" -ItemType Directory -Force

# Copiar todas las imágenes
Copy-Item -Path "frontend\src\img\*" -Destination "src\main\resources\static\img\" -Recurse -Force
```

### Opción 2: Copiar con el explorador de archivos

1. Abre: `frontend\src\img\`
2. Selecciona todas las imágenes (Ctrl+A)
3. Copia (Ctrl+C)
4. Ve a: `src\main\resources\static\`
5. Crea una carpeta llamada `img`
6. Pega las imágenes dentro (Ctrl+V)

---

## 🚀 Después de Copiar las Imágenes

### 1. Eliminar productos existentes (opcional, para actualizar con imágenes)

Si quieres que los productos existentes tengan las nuevas imágenes locales:

**Opción A: Desde MySQL:**
```sql
USE sistema_pedidos;
DELETE FROM detalle_pedido;
DELETE FROM pedido;
DELETE FROM producto;
```

**Opción B: Editar manualmente desde la interfaz:**
- Ve a: `http://localhost:3000/admin/products`
- Edita cada producto y asigna una imagen

### 2. Reiniciar el backend

```powershell
# Detener el backend (Ctrl+C)
# Luego:
mvn spring-boot:run
```

Esto hará que:
- Spring Boot copie las imágenes de `src/main/resources/static/img` a `target/classes/static/img`
- Los productos se creen con las nuevas rutas de imagen
- El servidor sirva las imágenes en `http://localhost:8080/img/pX.jpg`

### 3. Verificar que funciona

Abre en el navegador:
- `http://localhost:8080/img/banner.jpg` - Debe mostrar el banner
- `http://localhost:8080/img/p1.jpg` - Debe mostrar la imagen del producto 1
- `http://localhost:3000/` - El banner debe aparecer
- `http://localhost:3000/catalog` - Los productos deben mostrar imágenes

---

## 📝 Uso del Formulario de Productos

### Crear Producto:
1. Ve a: `http://localhost:3000/admin/products`
2. Click en "Nuevo Producto"
3. Llena los campos
4. **Selecciona una imagen** del selector (o escribe la ruta manualmente)
5. Guarda

### Editar Producto:
1. En la lista de productos, click en el botón de editar (lápiz)
2. Modifica los campos necesarios
3. Cambia la imagen si lo deseas
4. Guarda

---

## 🎨 Imágenes Disponibles

El formulario incluye un selector visual para estas imágenes:
- `/img/p1.jpg` a `/img/p20.jpg` - Productos
- `/img/banner.jpg` - Banner principal

También puedes escribir manualmente cualquier URL o ruta de imagen.

---

## ⚠️ Notas Importantes

1. **Las imágenes deben estar en `src/main/resources/static/img`** para que Spring Boot las sirva correctamente.

2. **Después de copiar imágenes, reinicia el backend** para que se copien a `target/classes/static/img`.

3. **El frontend busca las imágenes en el backend** (`http://localhost:8080/img/...`), no en su propia carpeta `frontend/src/img`.

4. **Para producción:** considera usar un CDN o servicio de almacenamiento externo para las imágenes.

---

## 🔧 Solución de Problemas

### Las imágenes no se cargan (404)
- Verifica que las imágenes estén en `target/classes/static/img/` (después de compilar)
- Abre: `http://localhost:8080/img/p1.jpg` directamente
- Si no funciona, reinicia el backend

### El banner no aparece en Home
- Verifica que `banner.jpg` esté en `src/main/resources/static/img/`
- Revisa la consola del navegador (F12) para errores

### Los productos no muestran imágenes
- Verifica que `imageUrl` no sea `null` en la base de datos
- Si usas productos viejos, edítalos y asigna una imagen
- O elimina los productos y reinicia el backend para crear nuevos con imágenes

---

## ✅ Checklist Final

- [ ] Copiar imágenes de `frontend/src/img/` a `src/main/resources/static/img/`
- [ ] Reiniciar backend (`mvn spring-boot:run`)
- [ ] Verificar `http://localhost:8080/img/banner.jpg`
- [ ] Verificar `http://localhost:8080/img/p1.jpg`
- [ ] Abrir `http://localhost:3000/` y ver el banner
- [ ] Abrir `http://localhost:3000/catalog` y ver productos con imágenes
- [ ] Probar crear/editar productos desde `/admin/products`

**¡Listo! Tu e-commerce ahora tiene imágenes personalizadas.** 🎉

