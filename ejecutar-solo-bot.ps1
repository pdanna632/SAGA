Write-Host "🤖 Iniciando Solo Bot Telegram y API..." -ForegroundColor Green
Write-Host ""

# Establecer ubicación del proyecto
Set-Location "d:\uni\Ing Software\Projecto\Projecto\SAGA"

# Compilar si es necesario
Write-Host "🔨 Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "🚀 Iniciando Spring Boot..." -ForegroundColor Yellow
    Write-Host "📡 API REST disponible en: http://localhost:8080/api/" -ForegroundColor Cyan
    Write-Host "🤖 Bot Telegram: @saga_arbitros_bot" -ForegroundColor Cyan
    Write-Host ""
    
    mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    Read-Host "Presiona Enter para salir..."
}
