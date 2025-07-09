@echo off
echo.
echo ==========================================
echo    SAGA WhatsApp Bot - Iniciando...
echo ==========================================
echo.

REM Verificar que estamos en el directorio correcto
if not exist "package.json" (
    echo ERROR: No se encontro package.json en el directorio actual
    echo Asegurate de ejecutar este script desde la carpeta whatsapp-bot
    pause
    exit /b 1
)

REM Verificar Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js no esta instalado
    echo Descarga Node.js desde: https://nodejs.org/
    pause
    exit /b 1
)

echo Node.js encontrado: 
node --version

REM Verificar si las dependencias estan instaladas
if not exist "node_modules" (
    echo.
    echo Las dependencias no estan instaladas. Instalando...
    npm install
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la instalacion de dependencias
        pause
        exit /b 1
    )
    echo Dependencias instaladas correctamente.
)

echo.
echo ==========================================
echo    SAGA WhatsApp Bot - Listo para usar
echo ==========================================
echo.
echo Instrucciones:
echo 1. Escanea el codigo QR con WhatsApp
echo 2. Ve a WhatsApp ^> Dispositivos vinculados
echo 3. Selecciona "Vincular un dispositivo"
echo 4. Escanea el codigo QR que aparece abajo
echo.
echo Presiona Ctrl+C para detener el bot
echo.

npm start
