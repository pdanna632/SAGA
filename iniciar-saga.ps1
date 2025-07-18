Write-Host "Iniciando Sistema SAGA..." -ForegroundColor Green
Write-Host ""

# Establecer ubicacion del proyecto (ruta relativa)
$projectPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectPath

# Compilar proyecto Java
Write-Host "Compilando proyecto Java..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error en compilacion. Verifica que Maven y Java esten instalados." -ForegroundColor Red
    Read-Host "Presiona Enter para continuar..."
    exit 1
}

Write-Host "Compilacion exitosa" -ForegroundColor Green
Write-Host ""

# Iniciar servidor web si Node.js esta disponible
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "Iniciando servidor web React..." -ForegroundColor Yellow
    
    # Verificar si las dependencias están instaladas
    if (-not (Test-Path "Pagina web SAGA\node_modules")) {
        Write-Host "Instalando dependencias de Node.js..." -ForegroundColor Yellow
        Set-Location "Pagina web SAGA"
        npm install
        Set-Location ".."
    }
    
    # Crear script temporal para React
    $reactScriptContent = @"
Set-Location '$projectPath\Pagina web SAGA'
Write-Host 'Iniciando servidor React...' -ForegroundColor Green
npm run dev
"@
    
    $reactScriptPath = "$env:TEMP\start-react-saga.ps1"
    $reactScriptContent | Out-File -FilePath $reactScriptPath -Encoding UTF8
    
    Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", $reactScriptPath -WindowStyle Normal
    Write-Host "Servidor web React iniciado en nueva ventana (puerto 5173)" -ForegroundColor Green
} else {
    Write-Host "Node.js no encontrado - servidor web omitido" -ForegroundColor Yellow
}

# Iniciar servidor Spring Boot con Bot Telegram
Write-Host "Iniciando servidor Spring Boot con Bot Telegram..." -ForegroundColor Yellow

# Crear un script temporal para evitar problemas de escaping
$tempScriptContent = @"
Set-Location '$projectPath'
Write-Host 'Iniciando Spring Boot con Bot Telegram...' -ForegroundColor Green
mvn spring-boot:run `"-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication`"
"@

$tempScriptPath = "$env:TEMP\start-springboot-saga.ps1"
$tempScriptContent | Out-File -FilePath $tempScriptPath -Encoding UTF8

Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", $tempScriptPath -WindowStyle Normal
Write-Host "Servidor Spring Boot iniciado en nueva ventana (puerto 8080)" -ForegroundColor Green

Write-Host ""
Write-Host "INFORMACION DEL SISTEMA:" -ForegroundColor Cyan
Write-Host "- Sistema Java: Menu principal (esta ventana)" -ForegroundColor White
Write-Host "- Login: ARBIANTIOQUIA / ADMIN" -ForegroundColor White
Write-Host "- Servidor Spring Boot: http://localhost:8080 (nueva ventana)" -ForegroundColor White
Write-Host "- Bot Telegram: @saga_arbitros_bot (activo)" -ForegroundColor White
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "- Servidor web React: http://localhost:5173 (nueva ventana)" -ForegroundColor White
}
Write-Host ""

# Iniciar menu principal Java
Write-Host "Iniciando menu principal..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

mvn exec:java "-Dexec.mainClass=com.saga.model.Main"
