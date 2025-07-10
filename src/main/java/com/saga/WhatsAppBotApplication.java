package com.saga;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhatsAppBotApplication {
    
    private static final Logger logger = Logger.getLogger(WhatsAppBotApplication.class.getName());
    
    public static void main(String[] args) {
        logger.info("🚀 Iniciando Bot WhatsApp SAGA...");
        
        try {
            SpringApplication.run(WhatsAppBotApplication.class, args);
            logger.info("✅ Bot WhatsApp SAGA iniciado exitosamente!");
            logger.info("🌐 Servidor disponible en: http://localhost:8080");
            logger.info("📡 Webhook endpoint: http://localhost:8080/webhook/whatsapp");
            logger.info("🔍 Health check: http://localhost:8080/webhook/health");
            
        } catch (Exception e) {
            logger.severe("❌ Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
