# 📋 GUÍA DE MODOS DE EJECUCIÓN - Sistema SAGA

## 🎯 OPCIONES DISPONIBLES

El sistema SAGA ahora ofrece **DOS MODOS DE EJECUCIÓN**:

### 1. 🖥️ **Sistema SAGA Tradicional**
- **Descripción:** El sistema original con menú de consola Java
- **Características:**
  - Interfaz de línea de comandos
  - Gestión completa de árbitros y partidos
  - Generación de informes
  - Asignación de árbitros
  - Modificación de disponibilidades
- **Uso:** Ideal para administradores del sistema

### 2. 🤖 **Sistema SAGA con WhatsApp Bot**
- **Descripción:** Sistema ampliado con bot de WhatsApp para árbitros
- **Características:**
  - Registro automático de árbitros via WhatsApp
  - Actualización de números telefónicos
  - Conversación natural y amigable
  - Integración con Excel existente
  - Normalización automática de números
- **Uso:** Ideal para que los árbitros se registren y actualicen su información

## 🚀 SCRIPTS DE EJECUCIÓN

### 📋 **Menú Principal (Recomendado)**
```powershell
.\iniciar-saga.ps1
```
- Muestra un menú para elegir entre ambas opciones
- Más fácil de usar para principiantes

### 🖥️ **Solo Sistema Tradicional**
```powershell
.\iniciar-saga-tradicional.ps1
```
- Ejecuta directamente el sistema original
- Más rápido si solo necesitas el menú Java

### 🤖 **Solo Sistema con WhatsApp**
```powershell
.\iniciar-saga-whatsapp.ps1
```
- Ejecuta directamente el sistema con bot
- Incluye tanto el servidor Java como el bot Node.js

### 📦 **Instalación de Dependencias**
```powershell
.\instalar-dependencias.ps1
```
- Solo necesario para el modo WhatsApp
- Instala dependencias de Node.js

## 🔧 COMANDOS MANUALES

### Para Sistema Tradicional:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.saga.model.Main"
```

### Para Sistema con WhatsApp:
```bash
# Terminal 1: Servidor Java
mvn spring-boot:run -Dspring-boot.run.mainClass=com.saga.WhatsAppApplication

# Terminal 2: Bot WhatsApp
cd whatsapp-bot
npm start
```

## 🎛️ CUÁNDO USAR CADA MODO

### 🖥️ **Usar Sistema Tradicional cuando:**
- Eres administrador del sistema
- Necesitas gestionar partidos y asignaciones
- Quieres generar informes
- Trabajas desde la oficina
- No necesitas funcionalidad de WhatsApp

### 🤖 **Usar Sistema con WhatsApp cuando:**
- Quieres que los árbitros se registren automáticamente
- Necesitas actualizar números telefónicos masivamente
- Quieres automatizar la comunicación
- Los árbitros necesitan acceso remoto
- Quieres modernizar el sistema

## 🔄 MIGRACIÓN Y COMPATIBILIDAD

- ✅ **Ambos sistemas usan los mismos archivos Excel**
- ✅ **Los datos son compatibles entre ambos modos**
- ✅ **Puedes alternar entre ambos sistemas sin problemas**
- ✅ **No se pierden datos al cambiar de modo**

## 📊 COMPARACIÓN RÁPIDA

| Característica | Sistema Tradicional | Sistema con WhatsApp |
|---|---|---|
| **Interfaz** | Consola Java | Consola + WhatsApp |
| **Usuarios** | Administradores | Administradores + Árbitros |
| **Registro** | Manual | Automático via WhatsApp |
| **Actualización** | Manual | Automática via WhatsApp |
| **Complejidad** | Baja | Media |
| **Modernidad** | Básica | Avanzada |

## 🛠️ SOLUCIÓN DE PROBLEMAS

### ❌ **Error: "Main class not found"**
- Verifica que el proyecto esté compilado: `mvn clean compile`
- Usa el script correcto según el modo deseado

### ❌ **Error: "Node.js not found"**
- Solo afecta al modo WhatsApp
- Instala Node.js desde https://nodejs.org
- Ejecuta: `.\instalar-dependencias.ps1`

### ❌ **Error: "WhatsApp QR not showing"**
- Solo afecta al modo WhatsApp
- Verifica que el bot esté ejecutándose
- Revisa la conexión a Internet

---

## 🎉 **¡AMBOS SISTEMAS FUNCIONAN PERFECTAMENTE!**

Puedes usar el sistema tradicional como siempre lo has hecho, o probar el nuevo sistema con WhatsApp para modernizar la gestión de árbitros. ¡Tú eliges!
