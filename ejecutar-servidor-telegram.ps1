Write-Host "Iniciando Servidor SAGA con Bot Telegram..." -ForegroundColor Green
Write-Host ""

# Establecer ubicacion del proyecto
Set-Location "d:\uni\Ing Software\Projecto\Projecto\SAGA"

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

Write-Host "INFORMACION DEL SERVIDOR:" -ForegroundColor Cyan
Write-Host "- Servidor Spring Boot: http://localhost:8080" -ForegroundColor White
Write-Host "- Bot Telegram: @saga_arbitros_bot" -ForegroundColor White
Write-Host "- API REST: http://localhost:8080/api/" -ForegroundColor White
Write-Host "- Comandos bot: /start, /ayuda, /mi_info, /partidos" -ForegroundColor White
Write-Host ""

# Iniciar servidor Spring Boot con bot Telegram
Write-Host "Iniciando servidor Spring Boot con Bot Telegram..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

& mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
