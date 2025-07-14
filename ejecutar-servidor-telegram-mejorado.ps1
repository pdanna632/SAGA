Write-Host "=== SAGA - Script Mejorado para Spring Boot ===" -ForegroundColor Green
Write-Host ""

# Obtener la ruta actual del script
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectPath = $scriptPath

Write-Host "Directorio del proyecto: $projectPath" -ForegroundColor Cyan
Set-Location $projectPath

# Verificar que estamos en el directorio correcto
if (-Not (Test-Path "pom.xml")) {
    Write-Host "ERROR: No se encuentra pom.xml en el directorio actual." -ForegroundColor Red
    Write-Host "Directorio actual: $(Get-Location)" -ForegroundColor Yellow
    Read-Host "Presiona Enter para continuar..."
    exit 1
}

Write-Host "✅ Archivo pom.xml encontrado" -ForegroundColor Green

# Compilar proyecto
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en compilación." -ForegroundColor Red
    Write-Host "Verifica que Maven y Java 17+ estén instalados correctamente." -ForegroundColor Yellow
    Write-Host "Comandos para verificar:" -ForegroundColor Cyan
    Write-Host "  java -version" -ForegroundColor White
    Write-Host "  mvn -version" -ForegroundColor White
    Read-Host "Presiona Enter para continuar..."
    exit 1
}

Write-Host "✅ Compilación exitosa" -ForegroundColor Green
Write-Host ""

Write-Host "INFORMACIÓN DEL SERVIDOR:" -ForegroundColor Cyan
Write-Host "- Servidor Spring Boot: http://localhost:8080" -ForegroundColor White
Write-Host "- Bot Telegram: @saga_arbitros_bot" -ForegroundColor White
Write-Host "- API REST: http://localhost:8080/api/" -ForegroundColor White
Write-Host "- Comandos bot: /start, /ayuda, /mi_info, /partidos" -ForegroundColor White
Write-Host ""

# Iniciando Spring Boot
Write-Host "Iniciando Spring Boot con Bot Telegram..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

# Usar sintaxis más simple sin problemas de escape
& mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
