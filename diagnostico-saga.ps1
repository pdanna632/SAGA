Write-Host "=== SAGA - Diagnóstico del Sistema ===" -ForegroundColor Green
Write-Host ""

# Verificar versiones de las herramientas
Write-Host "1. Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = & java -version 2>&1
    Write-Host "✅ Java instalado:" -ForegroundColor Green
    Write-Host $javaVersion[0] -ForegroundColor White
} catch {
    Write-Host "❌ Java no encontrado o no instalado" -ForegroundColor Red
}

Write-Host "`n2. Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = & mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Maven instalado:" -ForegroundColor Green
    Write-Host $mavenVersion -ForegroundColor White
} catch {
    Write-Host "❌ Maven no encontrado o no instalado" -ForegroundColor Red
}

Write-Host "`n3. Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = & node --version 2>&1
    Write-Host "✅ Node.js instalado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Node.js no encontrado (opcional para web)" -ForegroundColor Yellow
}

# Verificar directorio del proyecto
Write-Host "`n4. Verificando estructura del proyecto..." -ForegroundColor Yellow
$projectPath = "d:\uni\Ing Software\Projecto\projecto\SAGA"
Set-Location $projectPath

if (Test-Path "pom.xml") {
    Write-Host "✅ pom.xml encontrado" -ForegroundColor Green
} else {
    Write-Host "❌ pom.xml no encontrado" -ForegroundColor Red
    Write-Host "Directorio actual: $(Get-Location)" -ForegroundColor Yellow
}

if (Test-Path "src\main\java\com\saga\telegram\TelegramBotApplication.java") {
    Write-Host "✅ TelegramBotApplication.java encontrado" -ForegroundColor Green
} else {
    Write-Host "❌ TelegramBotApplication.java no encontrado" -ForegroundColor Red
}

if (Test-Path "src\main\resources\application.properties") {
    Write-Host "✅ application.properties encontrado" -ForegroundColor Green
} else {
    Write-Host "❌ application.properties no encontrado" -ForegroundColor Red
}

# Probar compilación
Write-Host "`n5. Probando compilación..." -ForegroundColor Yellow
& mvn clean compile -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    Write-Host "Ejecuta 'mvn clean compile' para ver los errores detallados" -ForegroundColor Yellow
}

# Probar comando Spring Boot
Write-Host "`n6. Probando comando Spring Boot..." -ForegroundColor Yellow
Write-Host "Comando a ejecutar:" -ForegroundColor Cyan
Write-Host "mvn spring-boot:run `"-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication`"" -ForegroundColor White

Write-Host "`n¿Quieres ejecutar el servidor ahora? (s/n): " -ForegroundColor Yellow -NoNewline
$respuesta = Read-Host

if ($respuesta -eq "s" -or $respuesta -eq "S") {
    Write-Host "`nEjecutando servidor..." -ForegroundColor Green
    & mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.telegram.TelegramBotApplication"
} else {
    Write-Host "`nDiagnóstico completado." -ForegroundColor Green
}

Write-Host "`nPresiona Enter para continuar..." -ForegroundColor Gray
Read-Host
