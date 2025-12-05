# 📸 COPIAR IMÁGENES AL BACKEND

## Comando Rápido (PowerShell)

Desde la raíz del proyecto, ejecuta:

```powershell
# Crear carpeta img en backend si no existe
New-Item -Path "src\main\resources\static\img" -ItemType Directory -Force

# Copiar todas las imágenes
Copy-Item -Path "frontend\src\img\*" -Destination "src\main\resources\static\img\" -Force

# Verificar que se copiaron
Get-ChildItem "src\main\resources\static\img"
```

Deberías ver:
- banner.jpg
- p1.jpg a p20.jpg
- Y otras imágenes

## Reiniciar Backend

Después de copiar las imágenes:

```powershell
# Detener el backend (Ctrl+C)
# Luego reiniciar:
mvn spring-boot:run
```

## Verificar

Abre en el navegador:
- http://localhost:8080/img/banner.jpg
- http://localhost:8080/img/p1.jpg

Si se ven las imágenes, ¡todo está listo!

## En el Frontend

El banner se verá automáticamente en:
- http://localhost:3000/

Los productos con imágenes en:
- http://localhost:3000/catalog

¡Listo! 🎉

