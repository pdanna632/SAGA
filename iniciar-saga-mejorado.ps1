Write-Host "🚀 Iniciando Sistema SAGA Mejorado..." -ForegroundColor Green
Write-Host ""

# Establecer ubicacion del proyecto
$projectPath = "d:\uni\Ing Software\Projecto\Projecto\SAGA"
Set-Location $projectPath

# Verificar Java y Maven
Write-Host "🔍 Verificando herramientas..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java encontrado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java no encontrado. Instala Java 17 o superior." -ForegroundColor Red
    exit 1
}

try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    Write-Host "✅ Maven encontrado: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven no encontrado. Instala Apache Maven." -ForegroundColor Red
    exit 1
}

# Compilar proyecto Java
Write-Host "🔨 Compilando proyecto Java..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en compilación. Revisa los logs anteriores." -ForegroundColor Red
    Read-Host "Presiona Enter para continuar..."
    exit 1
}

Write-Host "✅ Compilación exitosa" -ForegroundColor Green
Write-Host ""

# Verificar si Node.js está disponible e instalar dependencias si es necesario
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "🌐 Preparando servidor web React..." -ForegroundColor Yellow
    
    # Verificar si node_modules existe
    if (-not (Test-Path "Pagina web SAGA\node_modules")) {
        Write-Host "📦 Instalando dependencias de Node.js..." -ForegroundColor Yellow
        Set-Location "Pagina web SAGA"
        npm install
        if ($LASTEXITCODE -ne 0) {
            Write-Host "❌ Error instalando dependencias de Node.js" -ForegroundColor Red
            Set-Location ".."
        } else {
            Write-Host "✅ Dependencias instaladas correctamente" -ForegroundColor Green
            Set-Location ".."
        }
    }
    
    # Iniciar servidor React
    Write-Host "🚀 Iniciando servidor web React..." -ForegroundColor Yellow
    $reactArgs = @(
        "-NoExit",
        "-Command",
        "Set-Location '$projectPath\Pagina web SAGA'; npm run dev"
    )
    Start-Process powershell -ArgumentList $reactArgs -WindowStyle Normal
    Write-Host "✅ Servidor web React iniciado en nueva ventana (puerto 5173)" -ForegroundColor Green
} else {
    Write-Host "⚠️ Node.js no encontrado - servidor web omitido" -ForegroundColor Yellow
    Write-Host "   Para instalar Node.js: https://nodejs.org/" -ForegroundColor Gray
}

# Iniciar servidor Spring Boot con Bot Telegram
Write-Host "🤖 Iniciando servidor Spring Boot con Bot Telegram..." -ForegroundColor Yellow

# Crear un script temporal para ejecutar Spring Boot
$tempScript = @"
Set-Location '$projectPath'
Write-Host '🤖 Ejecutando Spring Boot...' -ForegroundColor Cyan
mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
"@

$tempScriptPath = "$env:TEMP\start-springboot.ps1"
$tempScript | Out-File -FilePath $tempScriptPath -Encoding UTF8

$springBootArgs = @(
    "-NoExit",
    "-ExecutionPolicy", "Bypass",
    "-File", $tempScriptPath
)

Start-Process powershell -ArgumentList $springBootArgs -WindowStyle Normal
Write-Host "✅ Servidor Spring Boot iniciado en nueva ventana (puerto 8080)" -ForegroundColor Green

Write-Host ""
Write-Host "📊 INFORMACION DEL SISTEMA:" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🖥️  Sistema Java: Menu principal (esta ventana)" -ForegroundColor White
Write-Host "🔐 Login: ARBIANTIOQUIA / ADMIN" -ForegroundColor White
Write-Host "🌐 Servidor Spring Boot: http://localhost:8080 (nueva ventana)" -ForegroundColor White
Write-Host "🤖 Bot Telegram: @saga_arbitros_bot (activo)" -ForegroundColor White
Write-Host "📡 API REST: http://localhost:8080/api/partidos" -ForegroundColor White
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "⚛️  Servidor web React: http://localhost:5173 (nueva ventana)" -ForegroundColor White
}
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""

# Esperar un poco antes de iniciar el menú principal
Write-Host "⏳ Esperando que los servidores inicien..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Iniciar menu principal Java
Write-Host "🎯 Iniciando menu principal Java..." -ForegroundColor Yellow
Write-Host "=================================================" -ForegroundColor Green
mvn exec:java -Dexec.mainClass="com.saga.model.Main"
