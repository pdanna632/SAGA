package com.saga.telegram;

import java.util.logging.Logger;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.saga.telegram.bot.SimpleTelegramBot;

public class SimpleTelegramBotApplication {
    
    private static final Logger logger = Logger.getLogger(SimpleTelegramBotApplication.class.getName());
    
    public static void main(String[] args) {
        logger.info("🤖 Iniciando Bot Telegram SAGA (Versión Simple)...");
        
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            SimpleTelegramBot bot = new SimpleTelegramBot();
            botsApi.registerBot(bot);
            
            logger.info("✅ Bot Telegram SAGA iniciado exitosamente!");
            logger.info("🤖 Bot Username: @" + bot.getBotUsername());
            logger.info("🔗 Token configurado correctamente");
            logger.info("📡 Esperando mensajes...");
            
            // Mantener la aplicación corriendo
            Thread.currentThread().join();
            
        } catch (TelegramApiException e) {
            logger.severe("❌ Error al inicializar el bot de Telegram: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.info("🛑 Bot detenido por el usuario");
        } catch (Exception e) {
            logger.severe("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
