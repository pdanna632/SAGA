#!/usr/bin/env pwsh
# Script rápido para testing del sistema SAGA
Write-Host "🚀 INICIANDO TESTING RÁPIDO DE SAGA" -ForegroundColor Cyan

# Detener procesos existentes
Write-Host "🛑 Deteniendo procesos existentes..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*node*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "✅ Procesos detenidos" -ForegroundColor Green

# Compilar proyecto
Write-Host "🔨 Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎯 OPCIONES DE TESTING:" -ForegroundColor Cyan
Write-Host "1. Bot Telegram + API (Puerto 8080)" -ForegroundColor White
Write-Host "2. Solo la página web React (Puerto 5173)" -ForegroundColor White
Write-Host "3. Ambos sistemas completos" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Selecciona una opción (1-3)"

switch ($choice) {
    "1" {
        Write-Host "🤖 Iniciando Bot Telegram + API..." -ForegroundColor Green
        Write-Host "Presiona Ctrl+C para detener" -ForegroundColor Yellow
        mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
    }
    "2" {
        Write-Host "🌐 Iniciando página web React..." -ForegroundColor Green
        Set-Location "Pagina web SAGA"
        if (-not (Test-Path "node_modules")) {
            Write-Host "📦 Instalando dependencias..." -ForegroundColor Yellow
            npm install
        }
        Write-Host "Presiona Ctrl+C para detener" -ForegroundColor Yellow
        npm run dev
    }
    "3" {
        Write-Host "🔥 Iniciando sistema completo..." -ForegroundColor Green
        Write-Host "Backend en puerto 8080, Frontend en puerto 5173" -ForegroundColor Yellow
        
        # Iniciar backend en background
        Start-Process pwsh -ArgumentList "-Command", "cd 'd:\uni\Ing Software\Projecto\projecto\SAGA'; mvn spring-boot:run '-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication'" -WindowStyle Minimized
        
        # Esperar un poco y luego iniciar frontend
        Start-Sleep -Seconds 5
        Set-Location "Pagina web SAGA"
        if (-not (Test-Path "node_modules")) {
            Write-Host "📦 Instalando dependencias..." -ForegroundColor Yellow
            npm install
        }
        npm run dev
    }
    default {
        Write-Host "❌ Opción inválida" -ForegroundColor Red
        exit 1
    }
}
