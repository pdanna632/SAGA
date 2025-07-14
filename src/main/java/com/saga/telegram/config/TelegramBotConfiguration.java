package com.saga.telegram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.saga.telegram.bot.SAGATelegramBot;

@Configuration
public class TelegramBotConfiguration {
    
    @Bean
    public TelegramBotsApi telegramBotsApi(SAGATelegramBot sagaTelegramBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(sagaTelegramBot);
        return api;
    }
}
