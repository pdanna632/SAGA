package com.saga.telegram.bot;

import java.util.logging.Logger;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SimpleTelegramBot extends TelegramLongPollingBot {
    
    private static final Logger logger = Logger.getLogger(SimpleTelegramBot.class.getName());
    
    private static final String BOT_TOKEN = "7962245724:AAHMUmP7jBRJC21aV_4sqh7BVf0oWqtLMas";
    private static final String BOT_USERNAME = "saga_arbitros_bot";
    
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
    
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                String userName = update.getMessage().getFrom().getFirstName();
                String userHandle = update.getMessage().getFrom().getUserName();
                
                logger.info("📥 Mensaje recibido de: " + userName + " (@" + userHandle + ")");
                logger.info("📝 Contenido: " + messageText);
                logger.info("🆔 Chat ID: " + chatId);
                
                // Procesar el mensaje
                String response = processMessage(messageText, chatId, userName, userHandle);
                
                if (response != null && !response.isEmpty()) {
                    sendTextMessage(chatId, response);
                }
            }
            
        } catch (Exception e) {
            logger.severe("❌ Error procesando update de Telegram: " + e.getMessage());
        }
    }
    
    /**
     * Procesa un mensaje de texto recibido
     */
    private String processMessage(String messageText, Long chatId, String userName, String userHandle) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "🤔 No entendí tu mensaje. Escribe /ayuda para ver los comandos disponibles.";
        }
        
        String text = messageText.toLowerCase().trim();
        
        // Comandos básicos
        if (text.equals("/start") || text.equals("hola") || text.equals("hi") || text.equals("buenas")) {
            return generateWelcomeMessage(userName, chatId);
        }
        
        if (text.equals("/ayuda") || text.equals("ayuda") || text.equals("help")) {
            return generateHelpMessage();
        }
        
        if (text.equals("/info") || text.contains("mi info") || text.contains("mis datos")) {
            return generateUserInfo(chatId, userName);
        }
        
        if (text.contains("partidos") || text.contains("partido")) {
            return "⚽ *Mis Partidos*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás consultar tus partidos asignados aquí.";
        }
        
        if (text.contains("disponibilidad") || text.contains("disponible")) {
            return "📅 *Disponibilidad*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás gestionar tu disponibilidad aquí.";
        }
        
        if (text.contains("contacto") || text.contains("contactos")) {
            return "📞 *Contactos*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás consultar contactos de otros árbitros.";
        }
        
        // Respuesta por defecto
        return "🤔 No entendí tu consulta.\n\n" +
               "Escribe /ayuda para ver los comandos disponibles.";
    }
    
    /**
     * Envía un mensaje de texto simple
     */
    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.enableMarkdown(true);
        
        try {
            execute(message);
            logger.info("✅ Mensaje enviado a chat: " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Genera mensaje de bienvenida
     */
    private String generateWelcomeMessage(String userName, Long chatId) {
        return String.format(
            "🏆 *¡Hola %s!*\n\n" +
            "Bienvenido al bot de SAGA (Sistema Automatizado de Gestión Arbitral).\n\n" +
            "Soy tu asistente para consultas sobre arbitraje.\n\n" +
            "*Comandos disponibles:*\n" +
            "• `/info` - Tu información como árbitro\n" +
            "• `partidos` - Tus próximos partidos\n" +
            "• `disponibilidad` - Gestionar disponibilidad\n" +
            "• `contactos` - Buscar contactos\n" +
            "• `/ayuda` - Ver todos los comandos\n\n" +
            "¿En qué puedo ayudarte hoy?",
            userName
        );
    }
    
    /**
     * Genera mensaje de ayuda
     */
    private String generateHelpMessage() {
        return """
               🏆 *BOT TELEGRAM SAGA*
               
               📋 *Información personal:*
               • `/info` - Ver tus datos como árbitro
               • `mi info` - Ver tus datos
               
               ⚽ *Partidos:*
               • `partidos` - Ver tus próximos partidos
               
               📅 *Disponibilidad:*
               • `disponibilidad` - Gestionar tu disponibilidad
               
               📞 *Contactos:*
               • `contactos` - Buscar contacto de árbitro
               
               ❓ *Ayuda:*
               • `/ayuda` - Ver este mensaje
               • `/start` - Mensaje de bienvenida
               
               💡 *También puedes escribir de forma natural!*
               
               Ejemplo: "¿Cuáles son mis partidos de esta semana?"
               """;
    }
    
    /**
     * Genera información del usuario
     */
    private String generateUserInfo(Long chatId, String userName) {
        try {
            // Intentar buscar árbitro en la base de datos
            // Por ahora solo mostramos información básica
            
            return String.format(
                "👤 *Tu Información en Telegram*\n\n" +
                "📝 *Nombre:* %s\n" +
                "🆔 *Chat ID:* `%s`\n\n" +
                "❗ *Para acceder a tu información completa:*\n" +
                "Necesitamos vincular tu cuenta de Telegram con tu registro en SAGA.\n\n" +
                "💡 *¿Cómo vincular?*\n" +
                "Proporciona tu número de teléfono o cédula para vincular automáticamente.",
                userName,
                chatId
            );
            
        } catch (Exception e) {
            logger.warning("Error obteniendo información del usuario: " + e.getMessage());
            return "❌ Error obteniendo tu información.\n\n" +
                   "Por favor, intenta más tarde.";
        }
    }
}
