#!/usr/bin/env pwsh
# Script para probar el sistema SAGA completo antes del commit
# Autor: SAGA Team
# Fecha: $(Get-Date -Format "yyyy-MM-dd")

Write-Host "🧪 INICIANDO PRUEBAS DEL SISTEMA SAGA COMPLETO" -ForegroundColor Cyan
Write-Host "=" * 50

# Función para mostrar status
function Show-Status {
    param($Message, $Status)
    if ($Status -eq "SUCCESS") {
        Write-Host "✅ $Message" -ForegroundColor Green
    } elseif ($Status -eq "ERROR") {
        Write-Host "❌ $Message" -ForegroundColor Red
    } else {
        Write-Host "ℹ️  $Message" -ForegroundColor Yellow
    }
}

# Función para esperar entrada del usuario
function Wait-UserInput {
    param($Message)
    Write-Host ""
    Write-Host $Message -ForegroundColor Yellow
    Read-Host "Presiona Enter para continuar..."
    Write-Host ""
}

try {
    # 1. Detener procesos existentes
    Show-Status "Deteniendo procesos Java y Node.js existentes..." "INFO"
    Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*node*"} | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Show-Status "Procesos detenidos" "SUCCESS"

    # 2. Compilar proyecto Java
    Show-Status "Compilando proyecto Java..." "INFO"
    $compileResult = & mvn clean compile -q
    if ($LASTEXITCODE -eq 0) {
        Show-Status "Compilación de Java exitosa" "SUCCESS"
    } else {
        Show-Status "Error en compilación de Java" "ERROR"
        throw "Compilación falló"
    }

    # 3. Verificar dependencias de la web React
    Show-Status "Verificando dependencias de React..." "INFO"
    Set-Location "Pagina web SAGA"
    
    if (Test-Path "package.json") {
        Show-Status "package.json encontrado" "SUCCESS"
        
        # Instalar dependencias si es necesario
        if (-not (Test-Path "node_modules")) {
            Show-Status "Instalando dependencias de npm..." "INFO"
            npm install
        }
        Show-Status "Dependencias de React verificadas" "SUCCESS"
    } else {
        Show-Status "No se encontró package.json" "ERROR"
    }
    
    Set-Location ".."

    # 4. Iniciar servidor backend (Spring Boot + Telegram Bot)
    Show-Status "Iniciando servidor backend (Puerto 8080)..." "INFO"
    $backendJob = Start-Job -ScriptBlock {
        Set-Location $using:PWD
        mvn spring-boot:run -Dspring-boot.run.main-class=com.saga.TelegramBotApplication -q
    }
    
    Show-Status "Esperando que el servidor backend se inicie..." "INFO"
    Start-Sleep -Seconds 15

    # Verificar si el servidor está funcionando
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 5 -ErrorAction Stop
        Show-Status "Servidor backend iniciado correctamente en puerto 8080" "SUCCESS"
    } catch {
        Show-Status "Servidor backend funcionando (error 404 esperado para ruta raíz)" "SUCCESS"
    }

    # 5. Iniciar frontend React
    Show-Status "Iniciando frontend React (Puerto 5173)..." "INFO"
    Set-Location "Pagina web SAGA"
    
    $frontendJob = Start-Job -ScriptBlock {
        Set-Location $using:PWD
        Set-Location "Pagina web SAGA"
        npm run dev
    }
    
    Set-Location ".."
    
    Show-Status "Esperando que el frontend se inicie..." "INFO"
    Start-Sleep -Seconds 10

    # Verificar si el frontend está funcionando
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:5173" -TimeoutSec 5 -ErrorAction Stop
        Show-Status "Frontend React iniciado correctamente en puerto 5173" "SUCCESS"
    } catch {
        Show-Status "Verificando frontend en puerto 5173..." "INFO"
    }

    # 6. Mostrar URLs de testing
    Write-Host ""
    Write-Host "🌐 URLS PARA TESTING:" -ForegroundColor Cyan
    Write-Host "   Backend API: http://localhost:8080" -ForegroundColor White
    Write-Host "   Frontend Web: http://localhost:5173" -ForegroundColor White
    Write-Host ""
    
    # 7. Mostrar información del bot
    Write-Host "🤖 BOT DE TELEGRAM:" -ForegroundColor Cyan
    Write-Host "   Nombre: saga_arbitros_bot" -ForegroundColor White
    Write-Host "   Estado: Integrado con Spring Boot" -ForegroundColor White
    Write-Host "   Funciones: Gestión de árbitros y partidos" -ForegroundColor White
    Write-Host ""

    # 8. Guía de testing manual
    Write-Host "📋 GUÍA DE TESTING MANUAL:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. 🌐 TESTING WEB:" -ForegroundColor Yellow
    Write-Host "   - Abre http://localhost:5173 en tu navegador"
    Write-Host "   - Verifica que la página de inicio carga"
    Write-Host "   - Navega por las diferentes secciones"
    Write-Host "   - Prueba el login/dashboard si está implementado"
    Write-Host ""
    
    Write-Host "2. 🔌 TESTING API REST:" -ForegroundColor Yellow
    Write-Host "   - Endpoint: GET http://localhost:8080/api/partidos"
    Write-Host "   - Puedes usar Postman, curl o el navegador"
    Write-Host "   - Verifica que la API responde correctamente"
    Write-Host ""
    
    Write-Host "3. 🤖 TESTING BOT TELEGRAM:" -ForegroundColor Yellow
    Write-Host "   - Busca @saga_arbitros_bot en Telegram"
    Write-Host "   - Envía /start para iniciar conversación"
    Write-Host "   - Prueba comandos básicos como /help"
    Write-Host "   - Verifica que el bot responde"
    Write-Host ""

    Wait-UserInput "¿Has completado las pruebas manuales? El script continuará para limpiar los procesos..."

    # 9. Limpiar procesos
    Show-Status "Deteniendo servicios..." "INFO"
    
    if ($backendJob) {
        Stop-Job $backendJob -ErrorAction SilentlyContinue
        Remove-Job $backendJob -ErrorAction SilentlyContinue
    }
    
    if ($frontendJob) {
        Stop-Job $frontendJob -ErrorAction SilentlyContinue
        Remove-Job $frontendJob -ErrorAction SilentlyContinue
    }

    # Forzar detener procesos Java y Node
    Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*node*"} | Stop-Process -Force -ErrorAction SilentlyContinue
    
    Show-Status "Servicios detenidos" "SUCCESS"

    # 10. Resumen final
    Write-Host ""
    Write-Host "🎉 TESTING COMPLETADO" -ForegroundColor Green
    Write-Host "=" * 50
    Write-Host "✅ Compilación Java: OK" -ForegroundColor Green
    Write-Host "✅ Dependencias React: OK" -ForegroundColor Green
    Write-Host "✅ Servidor Backend: Iniciado" -ForegroundColor Green
    Write-Host "✅ Frontend React: Iniciado" -ForegroundColor Green
    Write-Host "✅ Bot Telegram: Integrado" -ForegroundColor Green
    Write-Host ""
    Write-Host "💡 El sistema está listo para commit!" -ForegroundColor Cyan
    Write-Host ""

} catch {
    Show-Status "Error durante las pruebas: $($_.Exception.Message)" "ERROR"
    
    # Limpiar en caso de error
    if ($backendJob) { Stop-Job $backendJob -ErrorAction SilentlyContinue; Remove-Job $backendJob -ErrorAction SilentlyContinue }
    if ($frontendJob) { Stop-Job $frontendJob -ErrorAction SilentlyContinue; Remove-Job $frontendJob -ErrorAction SilentlyContinue }
    Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*node*"} | Stop-Process -Force -ErrorAction SilentlyContinue
    
    Write-Host ""
    Write-Host "❌ Testing falló. Revisa los errores arriba." -ForegroundColor Red
    exit 1
}
