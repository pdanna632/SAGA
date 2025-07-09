# WhatsApp Bot SAGA

Bot de WhatsApp desarrollado con Node.js y la librería Baileys para el proyecto SAGA.

## 🎯 Características

- ✅ Conexión mediante código QR
- ✅ Sesión persistente (no necesita escanear cada vez)
- ✅ Escucha todos los mensajes entrantes
- ✅ Reconexión automática
- ✅ Manejo de diferentes tipos de mensajes (texto, imagen, video, audio, etc.)
- ✅ Respuesta automática (opcional)

## 📋 Requisitos

- Node.js 17+ (requerido por Baileys)
- npm o yarn

## 🚀 Instalación

1. Navegar a la carpeta del bot:
```bash
cd whatsapp-bot
```

2. Instalar dependencias:
```bash
npm install
```

## 🔧 Uso

### Desde la aplicación Java SAGA:
```
1. Ejecutar la aplicación Java principal
2. Ir al menú "Extras" (opción 5)
3. Seleccionar "Iniciar bot de WhatsApp" (opción 3)
4. Se abrirá una nueva ventana de CMD con el bot
5. Seguir las instrucciones en la nueva ventana
```

### Iniciar el bot manualmente:
```bash
# Opción 1: Script automático
.\iniciar-bot.bat

# Opción 2: Instalación completa
.\instalar-y-ejecutar.bat

# Opción 3: Comandos manuales
npm install
npm start
```

### Iniciar en modo desarrollo:
```bash
npm run dev
```

## 📱 Configuración inicial

1. Al ejecutar el bot por primera vez, se generará un código QR en la consola
2. Abrir WhatsApp en tu teléfono
3. Ir a **Dispositivos vinculados** → **Vincular un dispositivo**
4. Escanear el código QR que aparece en la consola
5. El bot se conectará automáticamente

## 📁 Estructura de archivos

```
whatsapp-bot/
├── bot.js              # Archivo principal del bot
├── package.json        # Dependencias y scripts
├── auth_info/          # Carpeta creada automáticamente para sesión
└── README.md          # Este archivo
```

## 🔄 Funcionamiento

### Conexión inicial:
1. El bot intenta conectarse usando credenciales guardadas
2. Si no hay credenciales, muestra un código QR para escanear
3. Una vez conectado, guarda la sesión en la carpeta `auth_info`

### Mensajes:
- El bot escucha TODOS los mensajes entrantes
- Muestra información del remitente y contenido
- Puede responder automáticamente (función opcional)

### Reconexión:
- Si se pierde la conexión, el bot intenta reconectarse automáticamente
- Solo se detiene si la sesión es cerrada manualmente desde WhatsApp

## 🛠️ Personalización

### Respuesta automática:
Descomenta la línea en el método `processMessage()`:
```javascript
// this.sendAutoResponse(messageInfo)
```

### Enviar mensajes:
```javascript
// Ejemplo de uso
bot.sendMessage('5491234567890@s.whatsapp.net', 'Hola desde SAGA!')
```

## 🔧 Configuración avanzada

### Cambiar comportamiento del navegador:
```javascript
browser: Browsers.macOS('Desktop') // o Windows, Ubuntu
```

### Habilitar logs detallados:
```javascript
logger: P({ level: 'info' }) // en lugar de 'silent'
```

## ⚠️ Consideraciones importantes

1. **Sesión única**: Solo puede haber una sesión activa por número de WhatsApp
2. **Límites de WhatsApp**: Respeta los límites de envío para evitar baneos
3. **Uso responsable**: No spam, no mensajes masivos automatizados
4. **Términos de servicio**: Usar bajo tu propio riesgo

## 🐛 Solución de problemas

### Error de conexión:
- Verificar conexión a internet
- Reiniciar el bot
- Eliminar carpeta `auth_info` y volver a escanear QR

### El bot no responde:
- Verificar que el número esté correctamente vinculado
- Revisar logs en la consola

### Problemas con Node.js:
- Verificar que sea versión 17 o superior:
```bash
node --version
```

## 📞 Soporte

Para problemas o mejoras, contactar al equipo de desarrollo de SAGA.

---

**Desarrollado por el equipo SAGA** 🚀
