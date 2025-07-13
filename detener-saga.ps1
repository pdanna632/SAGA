#!/usr/bin/env pwsh
# Script para detener todos los componentes de SAGA
Write-Host "🛑 DETENIENDO SISTEMA SAGA COMPLETO" -ForegroundColor Red
Write-Host "=" * 40

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

try {
    # Detener procesos Java (Spring Boot, Bot Telegram, Menú)
    Show-Status "Deteniendo procesos Java..." "INFO"
    $javaProcesses = Get-Process | Where-Object {$_.ProcessName -like "*java*"}
    if ($javaProcesses) {
        $javaProcesses | Stop-Process -Force
        Show-Status "Procesos Java detenidos: $($javaProcesses.Count)" "SUCCESS"
    } else {
        Show-Status "No se encontraron procesos Java ejecutándose" "INFO"
    }

    # Detener procesos Node.js (React)
    Show-Status "Deteniendo procesos Node.js..." "INFO"
    $nodeProcesses = Get-Process | Where-Object {$_.ProcessName -like "*node*"}
    if ($nodeProcesses) {
        $nodeProcesses | Stop-Process -Force
        Show-Status "Procesos Node.js detenidos: $($nodeProcesses.Count)" "SUCCESS"
    } else {
        Show-Status "No se encontraron procesos Node.js ejecutándose" "INFO"
    }

    # Detener procesos PowerShell adicionales (si los hay)
    Show-Status "Verificando procesos PowerShell de SAGA..." "INFO"
    $psProcesses = Get-Process | Where-Object {$_.ProcessName -like "*powershell*" -and $_.MainWindowTitle -like "*SAGA*"}
    if ($psProcesses) {
        $psProcesses | Stop-Process -Force
        Show-Status "Procesos PowerShell de SAGA detenidos: $($psProcesses.Count)" "SUCCESS"
    }

    Write-Host ""
    Write-Host "🎉 SISTEMA SAGA DETENIDO COMPLETAMENTE" -ForegroundColor Green
    Write-Host "=" * 40
    Write-Host "✅ Servidor Spring Boot: Detenido" -ForegroundColor Green
    Write-Host "✅ Bot Telegram: Desconectado" -ForegroundColor Green
    Write-Host "✅ Servidor React: Detenido" -ForegroundColor Green
    Write-Host "✅ Menú Java: Cerrado" -ForegroundColor Green
    Write-Host ""
    Write-Host "💡 Puedes volver a iniciar con .\iniciar-saga.ps1" -ForegroundColor Cyan

} catch {
    Show-Status "Error deteniendo procesos: $($_.Exception.Message)" "ERROR"
    exit 1
}

Write-Host ""
Read-Host "Presiona Enter para cerrar..."
