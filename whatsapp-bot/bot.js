import pkg from '@whiskeysockets/baileys'
import { Boom } from '@hapi/boom'
import P from 'pino'
import qrcode from 'qrcode-terminal'

const { 
    default: makeWASocket, 
    DisconnectReason, 
    useMultiFileAuthState,
    Browsers
} = pkg

class WhatsAppBot {
    constructor() {
        this.socket = null
        this.isConnected = false
        this.init()
    }

    async init() {
        console.log('🤖 Iniciando Bot de WhatsApp SAGA...')
        
        try {
            // Crear estado de autenticación usando archivos
            const { state, saveCreds } = await useMultiFileAuthState('./auth_info')
            
            // Configurar socket
            this.socket = makeWASocket({
                auth: state,
                logger: P({ level: 'silent' }), // Silenciar logs de pino
                browser: Browsers.macOS('Desktop'),
                printQRInTerminal: false, // Usaremos nuestro propio QR
                getMessage: async (key) => {
                    // Esta función debería recuperar mensajes de tu base de datos
                    // Por ahora retornamos undefined
                    return undefined
                }
            })

            // Escuchar actualización de credenciales
            this.socket.ev.on('creds.update', saveCreds)

            // Escuchar actualizaciones de conexión
            this.socket.ev.on('connection.update', this.handleConnection.bind(this))

            // Escuchar mensajes nuevos
            this.socket.ev.on('messages.upsert', this.handleMessages.bind(this))

            // Escuchar otros eventos importantes
            this.socket.ev.on('chats.upsert', this.handleChats.bind(this))
            this.socket.ev.on('contacts.upsert', this.handleContacts.bind(this))

        } catch (error) {
            console.error('❌ Error inicializando el bot:', error)
            setTimeout(() => this.init(), 5000) // Reintentar en 5 segundos
        }
    }

    async handleConnection(update) {
        const { connection, lastDisconnect, qr } = update

        // Mostrar código QR para escanear
        if (qr) {
            console.log('📱 Escanea el siguiente código QR con WhatsApp:')
            console.log('━'.repeat(50))
            qrcode.generate(qr, { small: true })
            console.log('━'.repeat(50))
            console.log('⏳ Esperando escaneo del código QR...')
        }

        if (connection === 'open') {
            this.isConnected = true
            console.log('✅ Bot conectado exitosamente a WhatsApp!')
            console.log('🔄 Escuchando mensajes...')
            
            // Información del usuario conectado
            const userInfo = this.socket.user
            console.log(`📱 Usuario conectado: ${userInfo.name} (${userInfo.id})`)
        }

        if (connection === 'close') {
            this.isConnected = false
            const shouldReconnect = lastDisconnect?.error?.output?.statusCode !== DisconnectReason.loggedOut
            
            if (shouldReconnect) {
                console.log('🔄 Conexión perdida, reintentando...')
                setTimeout(() => this.init(), 3000)
            } else {
                console.log('❌ Bot desconectado. Sesión cerrada manualmente.')
                process.exit(0)
            }
        }

        if (connection === 'connecting') {
            console.log('🔄 Conectando al servidor de WhatsApp...')
        }
    }

    handleMessages(messageUpdate) {
        const { type, messages } = messageUpdate

        if (type === 'notify') {
            // Nuevos mensajes
            for (const message of messages) {
                this.processMessage(message)
            }
        }
    }

    processMessage(message) {
        // Extraer información del mensaje
        const messageInfo = this.extractMessageInfo(message)
        
        if (messageInfo) {
            console.log('📩 Nuevo mensaje recibido:')
            console.log(`   👤 De: ${messageInfo.sender}`)
            console.log(`   💬 Contenido: ${messageInfo.content}`)
            console.log(`   🕒 Hora: ${messageInfo.timestamp}`)
            console.log('─'.repeat(50))

            // Aquí puedes agregar lógica para responder automáticamente
            // this.sendAutoResponse(messageInfo)
        }
    }

    extractMessageInfo(message) {
        try {
            // Verificar que el mensaje no sea de nosotros
            if (message.key.fromMe) return null

            const sender = message.key.remoteJid
            const senderName = message.pushName || sender.split('@')[0]
            const timestamp = new Date(message.messageTimestamp * 1000).toLocaleString()

            let content = ''

            // Extraer contenido según el tipo de mensaje
            if (message.message?.conversation) {
                content = message.message.conversation
            } else if (message.message?.extendedTextMessage) {
                content = message.message.extendedTextMessage.text
            } else if (message.message?.imageMessage) {
                content = '[Imagen]' + (message.message.imageMessage.caption || '')
            } else if (message.message?.videoMessage) {
                content = '[Video]' + (message.message.videoMessage.caption || '')
            } else if (message.message?.audioMessage) {
                content = '[Audio]'
            } else if (message.message?.documentMessage) {
                content = '[Documento]' + (message.message.documentMessage.fileName || '')
            } else if (message.message?.stickerMessage) {
                content = '[Sticker]'
            } else {
                content = '[Mensaje no soportado]'
            }

            return {
                sender: senderName,
                senderJid: sender,
                content,
                timestamp,
                messageId: message.key.id
            }
        } catch (error) {
            console.error('❌ Error procesando mensaje:', error)
            return null
        }
    }

    handleChats(chats) {
        console.log(`💬 ${chats.length} chat(s) actualizado(s)`)
    }

    handleContacts(contacts) {
        console.log(`👥 ${contacts.length} contacto(s) actualizado(s)`)
    }

    // Método para enviar mensajes (ejemplo)
    async sendMessage(jid, message) {
        if (!this.isConnected) {
            console.log('❌ Bot no conectado')
            return false
        }

        try {
            await this.socket.sendMessage(jid, { text: message })
            console.log(`✅ Mensaje enviado a ${jid}`)
            return true
        } catch (error) {
            console.error('❌ Error enviando mensaje:', error)
            return false
        }
    }

    // Método para respuesta automática (ejemplo)
    async sendAutoResponse(messageInfo) {
        const autoResponse = `¡Hola ${messageInfo.sender}! Gracias por tu mensaje. El equipo de SAGA responderá pronto.`
        
        setTimeout(() => {
            this.sendMessage(messageInfo.senderJid, autoResponse)
        }, 2000) // Responder después de 2 segundos
    }

    // Método para cerrar el bot
    async shutdown() {
        console.log('🔄 Cerrando bot...')
        if (this.socket) {
            await this.socket.logout()
        }
        process.exit(0)
    }
}

// Inicializar el bot
const bot = new WhatsAppBot()

// Manejar cierre del proceso
process.on('SIGINT', () => {
    console.log('\n🛑 Cerrando bot...')
    bot.shutdown()
})

process.on('SIGTERM', () => {
    console.log('\n🛑 Cerrando bot...')
    bot.shutdown()
})

// Manejar errores no capturados
process.on('uncaughtException', (error) => {
    console.error('❌ Error no capturado:', error)
    // No salir del proceso, solo loguear
})

process.on('unhandledRejection', (error) => {
    console.error('❌ Promesa rechazada no manejada:', error)
    // No salir del proceso, solo loguear
})

export default WhatsAppBot
