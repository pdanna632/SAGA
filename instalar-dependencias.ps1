Write-Host "📦 Instalando Dependencias SAGA..." -ForegroundColor Green
Write-Host ""

# Verificar Maven
Write-Host "🔍 Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn --version | Select-String "Apache Maven" | Out-String
    Write-Host "✅ Maven encontrado: $($mavenVersion.Trim())" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven no encontrado. Instala Maven y Java 17+" -ForegroundColor Red
    Write-Host "📋 Instrucciones:" -ForegroundColor Cyan
    Write-Host "   1. Instala Java 17+ desde: https://openjdk.org" -ForegroundColor White
    Write-Host "   2. Instala Maven desde: https://maven.apache.org" -ForegroundColor White
    Write-Host "   3. Configura variables de entorno PATH" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "🎯 Instalación completa" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
Write-Host "   • Ejecuta: .\iniciar-saga.ps1" -ForegroundColor White
Write-Host "   • Login: ARBIANTIOQUIA / ADMIN" -ForegroundColor White
Write-Host ""
Write-Host "🔮 Futuras funcionalidades:" -ForegroundColor Magenta
Write-Host "   • Integración con API oficial de Meta WhatsApp" -ForegroundColor White
Write-Host "   • Expansión de funcionalidades web" -ForegroundColor White
Write-Host ""

Read-Host "Presiona Enter para continuar..."
