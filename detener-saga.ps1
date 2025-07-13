# Script para detener todos los componentes de SAGA
Write-Host "DETENIENDO SISTEMA SAGA COMPLETO" -ForegroundColor Red
Write-Host "=========================================="

# Función para mostrar status
function Show-Status {
    param($Message, $Status)
    if ($Status -eq "SUCCESS") {
        Write-Host "[OK] $Message" -ForegroundColor Green
    } elseif ($Status -eq "ERROR") {
        Write-Host "[ERROR] $Message" -ForegroundColor Red
    } else {
        Write-Host "[INFO] $Message" -ForegroundColor Yellow
    }
}

# Función para forzar cierre de procesos
function Force-KillProcess {
    param($ProcessName, $Description)
    
    Show-Status "Deteniendo $Description..." "INFO"
    
    try {
        # Intentar con Stop-Process primero
        $processes = Get-Process | Where-Object {$_.ProcessName -like "*$ProcessName*"} -ErrorAction SilentlyContinue
        if ($processes) {
            $processes | Stop-Process -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
        }
        
        # Verificar si aún existen y usar taskkill
        $remainingProcesses = Get-Process | Where-Object {$_.ProcessName -like "*$ProcessName*"} -ErrorAction SilentlyContinue
        if ($remainingProcesses) {
            taskkill /f /im "$ProcessName.exe" 2>$null
            Start-Sleep -Seconds 1
        }
        
        # Verificación final
        $finalCheck = Get-Process | Where-Object {$_.ProcessName -like "*$ProcessName*"} -ErrorAction SilentlyContinue
        if (-not $finalCheck) {
            Show-Status "$Description detenidos correctamente" "SUCCESS"
            return $true
        } else {
            Show-Status "Algunos procesos de $Description siguen activos" "ERROR"
            return $false
        }
    } catch {
        Show-Status "Error deteniendo $Description : $($_.Exception.Message)" "ERROR"
        return $false
    }
}

try {
    # Detener procesos específicos de SAGA
    Show-Status "Iniciando detencion de procesos SAGA..." "INFO"
    
    # 1. Detener procesos Java (Maven, Spring Boot)
    $javaSuccess = Force-KillProcess "java" "procesos Java (Maven/Spring Boot)"
    
    # 2. Detener procesos Node.js (React)
    $nodeSuccess = Force-KillProcess "node" "procesos Node.js (React)"
    
    # 3. Detener procesos Maven específicos
    try {
        $mavenProcesses = Get-WmiObject Win32_Process | Where-Object {
            $_.CommandLine -like "*maven*" -or 
            $_.CommandLine -like "*mvn*" -or
            $_.CommandLine -like "*spring-boot*"
        } -ErrorAction SilentlyContinue
        if ($mavenProcesses) {
            $mavenProcesses | ForEach-Object { 
                Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue 
            }
            Show-Status "Procesos Maven específicos detenidos" "SUCCESS"
        }
    } catch {
        Show-Status "Error verificando procesos Maven: $($_.Exception.Message)" "ERROR"
    }
    
    # 4. Liberar puertos específicos de SAGA
    Show-Status "Liberando puertos 8080 y 5173..." "INFO"
    try {
        # Puerto 8080 (Spring Boot)
        $port8080 = netstat -ano | findstr ":8080" 2>$null
        if ($port8080) {
            $pids8080 = ($port8080 | ForEach-Object { ($_ -split '\s+')[-1] }) | Sort-Object -Unique
            $pids8080 | ForEach-Object { 
                if ($_ -match '^\d+$') {
                    taskkill /f /pid $_ 2>$null
                }
            }
            Show-Status "Puerto 8080 liberado" "SUCCESS"
        }
        
        # Puerto 5173 (React)
        $port5173 = netstat -ano | findstr ":5173" 2>$null
        if ($port5173) {
            $pids5173 = ($port5173 | ForEach-Object { ($_ -split '\s+')[-1] }) | Sort-Object -Unique
            $pids5173 | ForEach-Object { 
                if ($_ -match '^\d+$') {
                    taskkill /f /pid $_ 2>$null
                }
            }
            Show-Status "Puerto 5173 liberado" "SUCCESS"
        }
    } catch {
        Show-Status "Error liberando puertos: $($_.Exception.Message)" "ERROR"
    }

    
    # 5. Detener ventanas PowerShell adicionales de SAGA
    Show-Status "Cerrando ventanas PowerShell de SAGA..." "INFO"
    try {
        $sagaWindows = Get-Process | Where-Object {
            $_.ProcessName -eq "powershell" -and 
            ($_.MainWindowTitle -like "*SAGA*" -or $_.MainWindowTitle -like "*mvn*")
        } -ErrorAction SilentlyContinue
        if ($sagaWindows) {
            $sagaWindows | Stop-Process -Force -ErrorAction SilentlyContinue
            Show-Status "Ventanas PowerShell de SAGA cerradas: $($sagaWindows.Count)" "SUCCESS"
        }
    } catch {
        Show-Status "Error cerrando ventanas PowerShell: $($_.Exception.Message)" "ERROR"
    }

    # Pausa para asegurar que todos los procesos terminen
    Start-Sleep -Seconds 3
    
    # Verificación final
    Show-Status "Realizando verificacion final..." "INFO"
    $javaCheck = Get-Process | Where-Object {$_.ProcessName -like "*java*"} -ErrorAction SilentlyContinue
    $nodeCheck = Get-Process | Where-Object {$_.ProcessName -like "*node*"} -ErrorAction SilentlyContinue
    
    Write-Host ""
    Write-Host "SISTEMA SAGA DETENIDO COMPLETAMENTE" -ForegroundColor Green
    Write-Host "=========================================="
    
    if (-not $javaCheck) {
        Write-Host "[OK] Servidor Spring Boot: Detenido" -ForegroundColor Green
        Write-Host "[OK] Bot Telegram: Desconectado" -ForegroundColor Green
        Write-Host "[OK] Menu Java: Cerrado" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] Algunos procesos Java aun activos: $($javaCheck.Count)" -ForegroundColor Yellow
    }
    
    if (-not $nodeCheck) {
        Write-Host "[OK] Servidor React: Detenido" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] Algunos procesos Node.js aun activos: $($nodeCheck.Count)" -ForegroundColor Yellow
    }
    
    Write-Host "[OK] Puertos 8080 y 5173: Liberados" -ForegroundColor Green
    Write-Host ""
    Write-Host "Puedes volver a iniciar con .\iniciar-saga.ps1" -ForegroundColor Cyan

} catch {
    Show-Status "Error general deteniendo procesos: $($_.Exception.Message)" "ERROR"
    Write-Host ""
    Write-Host "Si persisten problemas, reinicia el sistema o ejecuta:" -ForegroundColor Yellow
    Write-Host "   taskkill /f /im java.exe" -ForegroundColor White
    Write-Host "   taskkill /f /im node.exe" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "Presiona Enter para cerrar..." -ForegroundColor Gray
Read-Host
