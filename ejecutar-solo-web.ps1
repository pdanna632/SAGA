Write-Host "⚛️ Iniciando Solo Pagina Web React..." -ForegroundColor Green
Write-Host ""

# Establecer ubicación del proyecto
Set-Location "d:\uni\Ing Software\Projecto\Projecto\SAGA\Pagina web SAGA"

# Verificar si las dependencias están instaladas
if (-not (Test-Path "node_modules")) {
    Write-Host "📦 Instalando dependencias..." -ForegroundColor Yellow
    npm install
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error instalando dependencias" -ForegroundColor Red
        Read-Host "Presiona Enter para salir..."
        exit 1
    }
}

Write-Host "🚀 Iniciando servidor de desarrollo..." -ForegroundColor Yellow
Write-Host "🌐 Pagina web disponible en: http://localhost:5173" -ForegroundColor Cyan
Write-Host ""

npm run dev
