// ejemplo-uso.js
// Ejemplo de cómo usar el bot de WhatsApp SAGA

import WhatsAppBot from './bot.js'

// El bot se inicializa automáticamente al importar
// Aquí puedes agregar lógica personalizada

// Ejemplo de función para enviar mensaje programado
async function enviarMensajeProgramado() {
    // Esperar 10 segundos para que el bot se conecte
    setTimeout(async () => {
        const numeroDestinatario = '5491234567890@s.whatsapp.net' // Cambiar por número real
        const mensaje = 'Mensaje automático desde SAGA Bot!'
        
        // Esto requiere tener acceso a la instancia del bot
        // bot.sendMessage(numeroDestinatario, mensaje)
        
        console.log('✅ Mensaje programado enviado')
    }, 10000)
}

// Ejemplo de función para responder a comandos específicos
function procesarComando(messageInfo) {
    const comando = messageInfo.content.toLowerCase()
    
    switch(comando) {
        case '/help':
        case '/ayuda':
            return '🤖 Comandos disponibles:\n/help - Mostrar ayuda\n/info - Información del bot\n/tiempo - Hora actual'
            
        case '/info':
            return '🚀 SAGA Bot v1.0\nBot oficial del proyecto SAGA\nDesarrollado con Baileys'
            
        case '/tiempo':
            return `🕒 Hora actual: ${new Date().toLocaleString()}`
            
        default:
            return null // No responder a otros mensajes
    }
}

// Ejemplo de función para filtrar mensajes
function debeResponder(messageInfo) {
    // Solo responder a mensajes que empiecen con /
    return messageInfo.content.startsWith('/')
}

// Ejemplo de función para logging personalizado
function logMensaje(messageInfo) {
    const log = {
        fecha: new Date().toISOString(),
        remitente: messageInfo.sender,
        mensaje: messageInfo.content,
        jid: messageInfo.senderJid
    }
    
    console.log('📋 Log del mensaje:', JSON.stringify(log, null, 2))
}

// Exportar funciones para uso en otros archivos
export {
    enviarMensajeProgramado,
    procesarComando,
    debeResponder,
    logMensaje
}

// Ejecutar ejemplo si se ejecuta directamente
if (import.meta.url === `file://${process.argv[1]}`) {
    console.log('🚀 Ejecutando ejemplo de uso del bot...')
    enviarMensajeProgramado()
}
