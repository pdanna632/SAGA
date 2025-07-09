# Script para ejecutar el Bot de WhatsApp SAGA
# Ejecutar desde la raíz del proyecto

Write-Host "🤖 Iniciando Bot WhatsApp SAGA..." -ForegroundColor Green

# Verificar que Java esté instalado
try {
    $javaVersion = java -version 2>&1
    Write-Host "☕ Java encontrado: $($javaVersion[0])" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error: Java no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instala Java 17 o superior" -ForegroundColor Red
    exit 1
}

# Verificar que Maven esté instalado
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "📦 Maven encontrado: $($mavenVersion[0])" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instala Apache Maven" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔧 Compilando proyecto..." -ForegroundColor Cyan

# Compilar el proyecto
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al compilar el proyecto" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Proyecto compilado exitosamente" -ForegroundColor Green
Write-Host ""

# Mostrar opciones
Write-Host "Selecciona una opción:" -ForegroundColor White
Write-Host "1. Ejecutar Bot WhatsApp (modo servidor)" -ForegroundColor Yellow
Write-Host "2. Ejecutar modo de prueba interactivo" -ForegroundColor Yellow
Write-Host "3. Instalar dependencias solamente" -ForegroundColor Yellow
Write-Host ""

$opcion = Read-Host "Ingresa tu opción (1-3)"

switch ($opcion) {
    "1" {
        Write-Host ""
        Write-Host "🚀 Iniciando Bot WhatsApp en modo servidor..." -ForegroundColor Green
        Write-Host "📡 El webhook estará disponible en: http://localhost:8080/webhook/whatsapp" -ForegroundColor Cyan
        Write-Host "🔍 Health check: http://localhost:8080/webhook/health" -ForegroundColor Cyan
        Write-Host "ℹ️  Info: http://localhost:8080/webhook/info" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Para detener el servidor, presiona Ctrl+C" -ForegroundColor Yellow
        Write-Host ""
        
        # Ejecutar la aplicación Spring Boot
        mvn spring-boot:run
    }
    "2" {
        Write-Host ""
        Write-Host "🧪 Iniciando modo de prueba interactivo..." -ForegroundColor Green
        Write-Host ""
        
        # Ejecutar en modo test
        mvn exec:java -Dexec.mainClass="com.saga.WhatsAppBotApplication" -Dexec.args="test"
    }
    "3" {
        Write-Host ""
        Write-Host "📦 Instalando dependencias..." -ForegroundColor Green
        
        mvn dependency:resolve
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Dependencias instaladas correctamente" -ForegroundColor Green
        } else {
            Write-Host "❌ Error al instalar dependencias" -ForegroundColor Red
        }
    }
    default {
        Write-Host "❌ Opción no válida" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "👋 Script finalizado" -ForegroundColor Green
