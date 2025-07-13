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
            
            logger.info("📨 Mensaje recibido de " + firstName + " (ID: " + chatId + "): " + messageText);
            
            try {
                String response = messageProcessor.processMessage(messageText, chatId, firstName, username);
                sendMessage(chatId, response);
            } catch (Exception e) {
                logger.severe("❌ Error procesando mensaje: " + e.getMessage());
                sendMessage(chatId, "❌ Lo siento, ocurrió un error procesando tu mensaje. Intenta de nuevo.");
            }
        }
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            execute(message);
            logger.info("✅ Mensaje enviado a " + chatId + ": " + text.substring(0, Math.min(50, text.length())));
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando mensaje: " + e.getMessage());
        }
    }
}
