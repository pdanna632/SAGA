@echo off
echo.
echo ==========================================
echo         SAGA - Sistema de Gestion Arbitral
echo ==========================================
echo.

echo Compilando el proyecto...
mvn compile

if %errorlevel% neq 0 (
    echo ERROR: Fallo la compilacion
    pause
    exit /b 1
)

echo.
echo Iniciando aplicacion SAGA...
echo.
echo Credenciales:
echo Usuario: ARBIANTIOQUIA
echo Contraseña: ADMIN
echo.
echo Para probar el bot de WhatsApp:
echo 1. Seleccionar opcion 5 (Extras)
echo 2. Seleccionar opcion 3 (Iniciar bot de WhatsApp)
echo.

mvn exec:java
