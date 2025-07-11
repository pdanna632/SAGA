Write-Host "Iniciando Sistema SAGA..." -ForegroundColor Green
Write-Host ""

# Establecer ubicacion del proyecto
Set-Location "d:\uni\Ing Software\Projecto\projecto\SAGA"

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
    Write-Host "Iniciando servidor web..." -ForegroundColor Yellow
    Set-Location "Pagina web SAGA"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "npm run dev" -WindowStyle Normal
    Set-Location ".."
    Write-Host "Servidor web iniciado en nueva ventana (puerto 5173)" -ForegroundColor Green
} else {
    Write-Host "Node.js no encontrado - servidor web omitido" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "INFORMACION DEL SISTEMA:" -ForegroundColor Cyan
Write-Host "- Sistema Java: Menu principal (esta ventana)" -ForegroundColor White
Write-Host "- Login: ARBIANTIOQUIA / ADMIN" -ForegroundColor White
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "- Servidor web: http://localhost:5173 (nueva ventana)" -ForegroundColor White
}
Write-Host ""

# Iniciar menu principal Java
Write-Host "Iniciando menu principal..." -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Green

mvn exec:java "-Dexec.mainClass=com.saga.model.Main"
