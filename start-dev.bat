@echo off
echo ========================================
echo  Iniciando E-Commerce (Modo Desarrollo)
echo ========================================
echo.
echo Este script iniciara:
echo  1. Spring Boot (Backend) en puerto 8080
echo  2. Vue.js (Frontend) en puerto 3000
echo.

REM Verificar si Maven esta instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    echo Por favor instala Maven o usa: mvn spring-boot:run
    pause
    exit /b 1
)

REM Verificar si Node.js esta instalado
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Node.js no esta instalado o no esta en el PATH
    echo Por favor instala Node.js desde https://nodejs.org/
    pause
    exit /b 1
)

echo [1/3] Instalando dependencias del frontend...
cd frontend
call npm install
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al instalar dependencias
    pause
    exit /b 1
)
cd ..

echo.
echo [2/3] Iniciando Spring Boot en segundo plano...
start "Spring Boot Backend" cmd /k "mvn spring-boot:run"

echo.
echo [3/3] Esperando 10 segundos para que Spring Boot inicie...
timeout /t 10 /nobreak

echo.
echo Iniciando Vue.js (Frontend)...
cd frontend
start "Vue.js Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo  Servidores iniciados exitosamente!
echo ========================================
echo.
echo  Backend:  http://localhost:8080
echo  Frontend: http://localhost:3000
echo.
echo Para detener los servidores, cierra las ventanas de consola.
echo.
pause

