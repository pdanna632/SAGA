package com.saga.telegram.bot;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.saga.telegram.config.TelegramConfig;
import com.saga.telegram.service.TelegramMessageProcessor;

@Component
@SuppressWarnings("deprecation")
public class SAGATelegramBot extends TelegramLongPollingBot {
    
    private static final Logger logger = Logger.getLogger(SAGATelegramBot.class.getName());
    
    private final TelegramConfig config;
    private final TelegramMessageProcessor messageProcessor;
    
    public SAGATelegramBot(TelegramConfig config, TelegramMessageProcessor messageProcessor) {
        super();
        this.config = config;
        this.messageProcessor = messageProcessor;
    }
    
    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }
    
    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        // Manejar mensajes de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();
            String phoneNumber = null;
            
            // Intentar obtener número de teléfono si está disponible
            if (update.getMessage().getContact() != null) {
                phoneNumber = update.getMessage().getContact().getPhoneNumber();
            }
            
            logger.info("📨 Mensaje recibido de " + firstName + " (ID: " + chatId + "): " + messageText);
            
            try {
                String response = messageProcessor.processMessage(messageText, chatId, firstName, username, phoneNumber);
                
                // Determinar qué botones mostrar según la respuesta
                org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup buttons = null;
                if (response.contains("Modificar Disponibilidad")) {
                    buttons = messageProcessor.getDiasButtons();
                } else {
                    buttons = messageProcessor.getMenuButtons(chatId);
                }
                
                sendMessageWithButtons(chatId, response, buttons);
            } catch (Exception e) {
                logger.severe("❌ Error procesando mensaje: " + e.getMessage());
                sendMessage(chatId, "❌ Lo siento, ocurrió un error procesando tu mensaje. Intenta de nuevo.");
            }
            
        // Manejar contactos compartidos
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            Long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            
            logger.info("📱 Contacto recibido de " + firstName + " (ID: " + chatId + "): " + phoneNumber);
            
            try {
                String response = messageProcessor.processContact(phoneNumber, chatId, firstName);
                sendMessageWithButtons(chatId, response, messageProcessor.getMenuButtons(chatId));
            } catch (Exception e) {
                logger.severe("❌ Error procesando contacto: " + e.getMessage());
                sendMessage(chatId, "❌ Lo siento, ocurrió un error procesando tu contacto. Intenta de nuevo.");
            }
            
        // Manejar callbacks de botones
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String firstName = update.getCallbackQuery().getFrom().getFirstName();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            
            logger.info("🔘 Botón presionado por " + firstName + " (ID: " + chatId + "): " + callbackData);
            
            try {
                // Responder al callback para quitar el "loading" del botón
                answerCallbackQuery(update.getCallbackQuery().getId());
                
                // Procesar el callback
                String response = messageProcessor.processCallback(callbackData, chatId, firstName);
                
                // Determinar qué botones mostrar según la respuesta
                org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup buttons = null;
                if (response.contains("Modificar disponibilidad")) {
                    buttons = messageProcessor.getDiasButtons();
                } else {
                    buttons = messageProcessor.getMenuButtons(chatId);
                }
                
                // Editar el mensaje con la nueva respuesta y botones
                editMessageWithButtons(chatId, messageId, response, buttons);
                
            } catch (Exception e) {
                logger.severe("❌ Error procesando callback: " + e.getMessage());
                answerCallbackQuery(update.getCallbackQuery().getId(), "❌ Error procesando solicitud");
            }
        }
    }
    
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        
        try {
            execute(message);
            logger.info("✅ Mensaje enviado a " + chatId + ": " + text.substring(0, Math.min(50, text.length())));
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Envía mensaje con botón para solicitar contacto
     */
    private void sendMessageWithContactButton(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        
        // Crear botón para solicitar contacto
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton contactButton = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton("📱 Compartir mi contacto");
        contactButton.setRequestContact(true);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton cedulaButton = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton("🆔 Escribir mi cédula");
        
        // Crear teclado
        org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup keyboard = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow row1 = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow();
        row1.add(contactButton);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow row2 = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow();
        row2.add(cedulaButton);
        
        keyboard.setKeyboard(java.util.Arrays.asList(row1, row2));
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setInputFieldPlaceholder("Elige una opción para verificarte...");
        
        message.setReplyMarkup(keyboard);
        
        try {
            execute(message);
            logger.info("✅ Mensaje con botones enviado a " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando mensaje con botones: " + e.getMessage());
        }
    }
    
    /**
     * Método público para enviar mensajes con botón de contacto
     */
    public void sendMessageWithContactRequest(Long chatId, String text) {
        sendMessageWithContactButton(chatId, text);
    }
    
    /**
     * Envía mensaje con botones inline
     */
    public void sendMessageWithButtons(Long chatId, String text, org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }
        
        try {
            execute(message);
            logger.info("✅ Mensaje con botones enviado a " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando mensaje con botones: " + e.getMessage());
        }
    }
    
    /**
     * Edita un mensaje existente con nuevos botones
     */
    public void editMessageWithButtons(Long chatId, Integer messageId, String text, org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard) {
        org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText editMessage = 
            new org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText();
        
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("Markdown");
        
        if (keyboard != null) {
            editMessage.setReplyMarkup(keyboard);
        }
        
        try {
            execute(editMessage);
            logger.info("✅ Mensaje editado con botones para " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error editando mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Responde a un callback query
     */
    public void answerCallbackQuery(String callbackQueryId) {
        org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery answer = 
            new org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error respondiendo callback: " + e.getMessage());
        }
    }
    
    /**
     * Responde a un callback query con mensaje
     */
    public void answerCallbackQuery(String callbackQueryId, String text) {
        org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery answer = 
            new org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setText(text);
        answer.setShowAlert(true);
        
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error respondiendo callback con texto: " + e.getMessage());
        }
    }
}
