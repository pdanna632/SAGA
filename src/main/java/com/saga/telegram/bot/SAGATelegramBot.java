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
                sendMessage(chatId, response);
            } catch (Exception e) {
                logger.severe("❌ Error procesando mensaje: " + e.getMessage());
                sendMessage(chatId, "❌ Lo siento, ocurrió un error procesando tu mensaje. Intenta de nuevo.");
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            // Manejar cuando el usuario comparte su contacto
            Long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            
            logger.info("📱 Contacto recibido de " + firstName + " (ID: " + chatId + "): " + phoneNumber);
            
            try {
                String response = messageProcessor.processContact(phoneNumber, chatId, firstName);
                sendMessage(chatId, response);
            } catch (Exception e) {
                logger.severe("❌ Error procesando contacto: " + e.getMessage());
                sendMessage(chatId, "❌ Lo siento, ocurrió un error procesando tu contacto. Intenta de nuevo.");
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
}
