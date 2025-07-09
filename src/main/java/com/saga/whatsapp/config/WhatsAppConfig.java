package com.saga.whatsapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhatsAppConfig {
    
    @Value("${whatsapp.access-token}")
    private String accessToken;
    
    @Value("${whatsapp.app-id}")
    private String appId;
    
    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;
    
    @Value("${whatsapp.business-account-id}")
    private String businessAccountId;
    
    @Value("${whatsapp.api-version}")
    private String apiVersion;
    
    @Value("${whatsapp.verify-token}")
    private String verifyToken;
    
    @Value("${whatsapp.webhook-url}")
    private String webhookUrl;
    
    // Getters
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public String getPhoneNumberId() {
        return phoneNumberId;
    }
    
    public String getBusinessAccountId() {
        return businessAccountId;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public String getVerifyToken() {
        return verifyToken;
    }
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    public String getApiBaseUrl() {
        return "https://graph.facebook.com/" + apiVersion;
    }
    
    public String getMessagesEndpoint() {
        return getApiBaseUrl() + "/" + phoneNumberId + "/messages";
    }
}
