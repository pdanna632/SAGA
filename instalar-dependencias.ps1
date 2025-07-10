Write-Host "Instalando Dependencias SAGA..." -ForegroundColor Green
Write-Host ""

# Verificar Maven
Write-Host "Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn --version | Select-String "Apache Maven" | Out-String
    Write-Host "Maven encontrado: $($mavenVersion.Trim())" -ForegroundColor Green
} catch {
    Write-Host "Maven no encontrado. Instala Maven y Java 17+" -ForegroundColor Red
    Write-Host "Instrucciones:" -ForegroundColor Cyan
    Write-Host "   1. Instala Java 17+ desde: https://openjdk.org" -ForegroundColor White
    Write-Host "   2. Instala Maven desde: https://maven.apache.org" -ForegroundColor White
    Write-Host "   3. Configura variables de entorno PATH" -ForegroundColor White
    exit 1
}

# Verificar Node.js
Write-Host ""
Write-Host "Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    Write-Host "Node.js encontrado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "Node.js no encontrado. Instalando dependencias web sera omitido." -ForegroundColor Yellow
    Write-Host "Para instalar Node.js: https://nodejs.org" -ForegroundColor Cyan
}

# Instalar dependencias web si Node.js esta disponible
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host ""
    Write-Host "Instalando dependencias web..." -ForegroundColor Yellow
    Set-Location "Pagina web SAGA"
    try {
        npm install
        Write-Host "Dependencias web instaladas correctamente" -ForegroundColor Green
    } catch {
        Write-Host "Error instalando dependencias web" -ForegroundColor Red
    }
    Set-Location ".."
}

Write-Host ""
Write-Host "Instalacion completa" -ForegroundColor Green
Write-Host ""
Write-Host "Proximos pasos:" -ForegroundColor Cyan
Write-Host "   - Ejecuta: .\iniciar-saga.ps1" -ForegroundColor White
Write-Host "   - Login: ARBIANTIOQUIA / ADMIN" -ForegroundColor White
Write-Host ""
Write-Host "Funcionalidades disponibles:" -ForegroundColor Magenta
Write-Host "   - Sistema Java completo de gestion arbitral" -ForegroundColor White
Write-Host "   - Interfaz web moderna (si Node.js esta instalado)" -ForegroundColor White
Write-Host ""

Read-Host "Presiona Enter para continuar..."
