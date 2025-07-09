# 🚀 INSTRUCCIONES RÁPIDAS - Sistema SAGA

## 🎯 OPCIONES DE EJECUCIÓN

### 🖥️ Sistema SAGA Tradicional (Menú Java Original)
```powershell
.\iniciar-saga-tradicional.ps1
```

### 🤖 Sistema SAGA con WhatsApp Bot
```powershell
.\iniciar-saga-whatsapp.ps1
```

### 🎛️ Menú de Selección (Ambas Opciones)
```powershell
.\iniciar-saga.ps1
```

## ⚡ Inicio Rápido WhatsApp

### 1️⃣ Instalar Dependencias
```powershell
.\instalar-dependencias.ps1
```

### 2️⃣ Iniciar Sistema
```powershell
.\iniciar-saga-whatsapp.ps1
```

### 3️⃣ Configurar WhatsApp
1. Escanear código QR que aparece en terminal
2. Enviar mensaje con "SAGA" para probar

## 🔧 Comandos Manuales

### Java (Backend)
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run -Dspring-boot.run.mainClass=com.saga.WhatsAppApplication
```

### Node.js (Bot)
```bash
cd whatsapp-bot
npm start
```

## 📱 Pruebas de Números

El sistema maneja estos formatos automáticamente:
- `+573001234567` ✅
- `573001234567` ✅  
- `3001234567` ✅
- `57 300 123 4567` ✅
- `+57 300 123 4567` ✅
- `(300) 123-4567` ✅

## 🗂️ Archivos Importantes

- `src/main/resources/data/Arbitros.xlsx` - Base de datos
- `whatsapp-bot/data/requests.json` - Solicitudes
- `whatsapp-bot/data/responses.json` - Respuestas

## 💬 Conversación de Ejemplo

```
👤 Usuario: "Hola SAGA"
🤖 Bot: "Hola, soy SAGA, tu asistente arbitral. No pareces estar registrado, por favor introduce tu cédula:"

👤 Usuario: "12345678"
🤖 Bot: "No encontré tu cédula en el sistema. Vamos a registrarte. Por favor, proporciona tu nombre completo:"

👤 Usuario: "Juan Pérez"
🤖 Bot: "Perfecto. Ahora ingresa tu categoría arbitral (ejemplo: Nacional, Regional, Local):"

👤 Usuario: "Nacional"
🤖 Bot: "Por favor confirma tus datos:
📝 Nombre: Juan Pérez
🆔 Cédula: 12345678
📱 Teléfono: 3001234567
🏆 Categoría: Nacional

¿Los datos son correctos? (Responde "sí" para confirmar o "no" para reiniciar)"

👤 Usuario: "sí"
🤖 Bot: "¡Excelente Juan! Tu registro ha sido completado exitosamente. Ahora eres parte del sistema SAGA. ¿En qué puedo ayudarte?"
```

## 🎯 Estado del Sistema

✅ **COMPLETADO:**
- Normalización de números telefónicos
- Conversación natural y amigable
- Registro completo de árbitros
- Actualización de teléfonos
- Integración Java ↔ Node.js
- Scripts de instalación y ejecución
- Documentación completa

🔥 **LISTO PARA USAR!**
