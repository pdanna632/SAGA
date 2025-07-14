Write-Host "Probando comando Spring Boot..." -ForegroundColor Green
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

# Probar comando Spring Boot directamente
Write-Host "Iniciando Spring Boot directamente..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

mvn spring-boot:run -Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication
