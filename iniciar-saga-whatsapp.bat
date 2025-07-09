@echo off
echo 🚀 Iniciando Sistema SAGA WhatsApp...
echo.

echo 📱 Paso 1: Compilando proyecto Java...
cd "d:\uni\Ing Software\Projecto\projecto\SAGA"
call mvn clean compile

if %errorlevel% neq 0 (
    echo ❌ Error compilando proyecto Java
    pause
    exit /b 1
)

echo.
echo 🤖 Paso 2: Iniciando bot de WhatsApp...
cd "d:\uni\Ing Software\Projecto\projecto\SAGA\whatsapp-bot"
start "WhatsApp Bot" cmd /k "npm start"

echo.
echo ⏳ Esperando 3 segundos para que el bot inicie...
timeout /t 3 /nobreak > nul

echo.
echo ☕ Paso 3: Iniciando servidor Java...
cd "d:\uni\Ing Software\Projecto\projecto\SAGA"
java -cp target/classes;target/dependency/* com.saga.WhatsAppApplication

echo.
echo ✅ Sistema SAGA WhatsApp iniciado completo!
pause
