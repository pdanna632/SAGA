package com.saga.telegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {
    
    @Value("${telegram.bot-token}")
    private String botToken;
    
    @Value("${telegram.bot-username}")
    private String botUsername;
    
    @Value("${telegram.webhook-url:}")
    private String webhookUrl;
    
    @Value("${telegram.polling.enabled:true}")
    private boolean pollingEnabled;
    
    @Value("${bot.admin-chat-id:}")
    private String adminChatId;
    
    @Value("${bot.welcome-message}")
    private String welcomeMessage;
    
    @Value("${bot.data.directory:bot-data/}")
    private String botDataDirectory;
    
    // Getters
    public String getBotToken() {
        return botToken;
    }
    
    public String getBotUsername() {
        return botUsername;
    }
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    public boolean isPollingEnabled() {
        return pollingEnabled;
    }
    
    public String getAdminChatId() {
        return adminChatId;
    }
    
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
    
    public String getBotDataDirectory() {
        return botDataDirectory;
    }
}
