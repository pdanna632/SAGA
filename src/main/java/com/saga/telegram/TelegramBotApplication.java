package com.saga.telegram;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.saga")
public class TelegramBotApplication {
    
    private static final Logger logger = Logger.getLogger(TelegramBotApplication.class.getName());
    
    public static void main(String[] args) {
        logger.info("🤖 Iniciando Bot Telegram SAGA con Spring Boot...");
        
        try {
            SpringApplication.run(TelegramBotApplication.class, args);
            logger.info("✅ Bot Telegram SAGA iniciado exitosamente!");
            logger.info("🌐 Servidor disponible en: http://localhost:8080");
            
        } catch (Exception e) {
            logger.severe("❌ Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
