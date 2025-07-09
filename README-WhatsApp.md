# 🤖 Bot de WhatsApp SAGA - Sistema de Gestión de Árbitros

## 📋 Descripción
Este sistema integra un bot de WhatsApp con el sistema SAGA para gestionar el registro y actualización de información de árbitros de fútbol.

## ✨ Funcionalidades

### 🔍 Detección Automática
- Detecta mensajes que contengan "SAGA"
- Verifica automáticamente si el número está registrado

### 👤 Registro de Árbitros
- Guía paso a paso para registrar nuevos árbitros
- Validación de datos (cédula, nombre, categoría)
- Conversación natural y amigable

### 🔄 Actualización de Teléfonos
- Detecta si un árbitro registrado usa un número diferente
- Permite actualizar el número de teléfono fácilmente

### 🧹 Normalización de Teléfonos
- Ignora automáticamente prefijos: +57, 57
- Limpia espacios, guiones y paréntesis
- Ejemplos:
  - `+573001234567` → `3001234567`
  - `57 300 123 4567` → `3001234567`
  - `(300) 123-4567` → `3001234567`

## 🚀 Instalación y Ejecución

### 📋 Requisitos Previos
- Java 17+
- Maven 3.6+
- Node.js 18+
- WhatsApp instalado en tu teléfono

### 🛠️ Instalación

1. **Instalar dependencias de Node.js:**
   ```powershell
   .\instalar-dependencias.ps1
   ```

2. **Iniciar el sistema completo:**
   ```powershell
   .\iniciar-saga-whatsapp.ps1
   ```

### 📱 Configuración de WhatsApp

1. Al iniciar el bot, aparecerá un código QR en la terminal
2. Abre WhatsApp en tu teléfono
3. Ve a **Configuración** → **Dispositivos vinculados**
4. Toca **Vincular un dispositivo**
5. Escanea el código QR mostrado en la terminal

## 🔧 Configuración

### 📁 Archivos de Configuración
- `src/main/resources/application.properties` - Configuración del servidor Java
- `whatsapp-bot/package.json` - Configuración del bot de Node.js

### 🗂️ Estructura de Archivos
```
SAGA/
├── src/main/java/com/saga/
│   ├── controller/WhatsAppController.java
│   ├── services/WhatsAppService.java
│   └── utils/
│       ├── ExcelArbitroWriter.java
│       └── WhatsAppFileHandler.java
├── whatsapp-bot/
│   ├── bot.js
│   ├── data/
│   │   ├── requests.json
│   │   └── responses.json
│   └── auth_info/ (generado automáticamente)
└── scripts/
    ├── iniciar-saga-whatsapp.ps1
    └── instalar-dependencias.ps1
```

## 💬 Flujo de Conversación

### 🆕 Usuario Nuevo
1. **Usuario:** "Hola SAGA"
2. **Bot:** "Hola, soy SAGA, tu asistente arbitral. No pareces estar registrado, por favor introduce tu cédula:"
3. **Usuario:** "12345678"
4. **Bot:** "No encontré tu cédula en el sistema. Vamos a registrarte. Por favor, proporciona tu nombre completo:"
5. **Usuario:** "Juan Pérez"
6. **Bot:** "Perfecto. Ahora ingresa tu categoría arbitral (ejemplo: Nacional, Regional, Local):"
7. **Usuario:** "Nacional"
8. **Bot:** "Por favor confirma tus datos..."
9. **Usuario:** "sí"
10. **Bot:** "¡Excelente Juan! Tu registro ha sido completado exitosamente."

### 🔄 Actualización de Teléfono
1. **Usuario:** "SAGA" (desde un número diferente)
2. **Bot:** "Hola, soy SAGA... introduce tu cédula:"
3. **Usuario:** "12345678"
4. **Bot:** "¡Hola Juan! Encontré tu registro... tu número actual es diferente. ¿Deseas actualizar tu número?"
5. **Usuario:** "sí"
6. **Bot:** "¡Perfecto Juan! Tu número ha sido actualizado exitosamente."

## 🔧 Mantenimiento

### 📊 Monitoreo
- El sistema procesa solicitudes cada 2 segundos
- Los logs aparecen en la consola de ambos servicios
- Los archivos JSON se limpian automáticamente

### 🗂️ Archivos de Datos
- `whatsapp-bot/data/requests.json` - Solicitudes pendientes
- `whatsapp-bot/data/responses.json` - Respuestas del servidor Java
- `src/main/resources/data/Arbitros.xlsx` - Base de datos de árbitros

### 🛠️ Solución de Problemas

#### ❌ Error: "Bot no conectado"
- Verifica que WhatsApp Web esté activo
- Revisa la conexión a Internet
- Reescanea el código QR

#### ❌ Error: "Missing mandatory Classpath entries"
- Ejecuta: `mvn clean compile`
- Verifica que Java 17+ esté instalado

#### ❌ Error: "npm install failed"
- Verifica que Node.js esté instalado
- Ejecuta: `npm cache clean --force`
- Intenta nuevamente: `npm install`

## 📞 Contacto y Soporte

Para reportar errores o sugerir mejoras, contacta al equipo de desarrollo SAGA.

---

**¡Listo para usar! 🎉**

El sistema ahora puede manejar:
- ✅ Normalización de números telefónicos
- ✅ Conversación natural y amigable
- ✅ Registro completo de árbitros
- ✅ Actualización de información
- ✅ Integración completa Java ↔ Node.js
