@echo off
echo.
echo ==========================================
echo    SAGA WhatsApp Bot - Instalador
echo ==========================================
echo.

echo Verificando Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js no esta instalado
    echo Descarga Node.js desde: https://nodejs.org/
    pause
    exit /b 1
)

echo Node.js encontrado: 
node --version

echo.
echo Instalando dependencias...
npm install

if %errorlevel% neq 0 (
    echo ERROR: Fallo la instalacion de dependencias
    pause
    exit /b 1
)

echo.
echo ==========================================
echo    Instalacion completada!
echo ==========================================
echo.
echo Para iniciar el bot, ejecuta:
echo   npm start
echo.
echo O para iniciarlo ahora, presiona cualquier tecla...
pause >nul

echo.
echo ==========================================
echo    Iniciando SAGA WhatsApp Bot...
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
