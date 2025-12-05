@echo off
echo ========================================
echo  Compilando E-Commerce (Modo Produccion)
echo ========================================
echo.
echo Este script:
echo  1. Compilara Vue.js (frontend)
echo  2. Copiara el build a Spring Boot
echo  3. Compilara Spring Boot
echo  4. Iniciara el servidor en puerto 8080
echo.

REM Verificar si Maven esta instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    pause
    exit /b 1
)

echo [1/3] Compilando proyecto completo con Maven...
echo (Esto incluye instalar Node.js, npm install, npm run build automaticamente)
call mvn clean install

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar el proyecto
    pause
    exit /b 1
)

echo.
echo [2/3] Build completado exitosamente!
echo.
echo [3/3] Iniciando Spring Boot con el frontend integrado...
call mvn spring-boot:run

echo.
echo Servidor detenido.
pause

