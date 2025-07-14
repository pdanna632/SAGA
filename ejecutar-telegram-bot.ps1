Write-Host "Iniciando Bot de Telegram SAGA..." -ForegroundColor Green
Write-Host ""

# Establecer ubicacion del proyecto
Set-Location "d:\uni\Ing Software\Projecto\projecto\SAGA"

# Compilar proyecto
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error en compilacion. Verifica que Maven y Java esten instalados." -ForegroundColor Red
    Read-Host "Presiona Enter para continuar..."
    exit 1
}

Write-Host "Compilacion exitosa" -ForegroundColor Green
Write-Host ""

Write-Host "INFORMACION DEL BOT TELEGRAM:" -ForegroundColor Cyan
Write-Host "- Bot: @saga_arbitros_bot" -ForegroundColor White
Write-Host "- Comandos: /start, /ayuda, /mi_info, /partidos" -ForegroundColor White
Write-Host "- Los arbitros pueden consultar su informacion directamente" -ForegroundColor White
Write-Host ""

# Iniciar bot de Telegram
Write-Host "Iniciando bot de Telegram..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

mvn exec:java "-Dexec.mainClass=com.saga.telegram.SimpleTelegramBotApplication"
