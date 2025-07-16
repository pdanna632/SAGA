package com.saga.telegram.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
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
                
                // Verificar si necesita enviar una poll
                if (response.contains("[ENVIAR_POLL_DIAS]")) {
                    // Remover el marcador de la respuesta
                    String cleanResponse = response.replace("[ENVIAR_POLL_DIAS]", "").trim();
                    
                    // Enviar mensaje primero sin botones
                    sendMessage(chatId, cleanResponse);
                    
                    // Luego enviar selección de días (que actúa como poll)
                    sendPollDias(chatId);
                } else if (response.contains("[ENVIAR_POLL_FRANJAS:")) {
                    // Extraer el día del marcador
                    int startIndex = response.indexOf("[ENVIAR_POLL_FRANJAS:") + "[ENVIAR_POLL_FRANJAS:".length();
                    int endIndex = response.indexOf("]", startIndex);
                    String dia = response.substring(startIndex, endIndex);
                    
                    // Remover el marcador de la respuesta
                    String cleanResponse = response.replaceAll("\\[ENVIAR_POLL_FRANJAS:[^\\]]+\\]", "").trim();
                    
                    // Enviar mensaje primero
                    sendMessage(chatId, cleanResponse);
                    
                    // Luego enviar poll de franjas horarias
                    sendPollFranjasHorarias(chatId, dia);
                } else if (response.contains("[MOSTRAR_BOTONES_CONFIRMACION]")) {
                    // Remover el marcador
                    String cleanResponse = response.replace("[MOSTRAR_BOTONES_CONFIRMACION]", "").trim();
                    
                    // Enviar mensaje con botones de confirmación final
                    sendMessageWithButtons(chatId, cleanResponse, createConfirmationButtons());
                } else {
                    // Determinar qué botones mostrar según la respuesta
                    org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup buttons = null;
                    if (response.contains("Modificar Disponibilidad")) {
                        buttons = messageProcessor.getDiasButtons();
                    } else {
                        buttons = messageProcessor.getMenuButtons(chatId);
                    }
                    
                    sendMessageWithButtons(chatId, response, buttons);
                }
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
            
        // Manejar respuestas de polls
        } else if (update.hasPollAnswer()) {
            String pollId = update.getPollAnswer().getPollId();
            Long userId = update.getPollAnswer().getUser().getId();
            List<Integer> optionIds = update.getPollAnswer().getOptionIds();
            String firstName = update.getPollAnswer().getUser().getFirstName();
            
            logger.info("📊 Respuesta de poll recibida de " + firstName + " (ID: " + userId + "): " + optionIds);
            
            try {
                // Convertir IDs de opciones a nombres de días (asumiendo orden: Jueves=0, Viernes=1, Sábado=2, Domingo=3)
                List<String> diasNombres = Arrays.asList("Jueves", "Viernes", "Sábado", "Domingo");
                List<String> diasSeleccionados = new ArrayList<>();
                
                for (Integer optionId : optionIds) {
                    if (optionId < diasNombres.size()) {
                        diasSeleccionados.add(diasNombres.get(optionId));
                    }
                }
                
                // Procesar la respuesta del poll
                String response = messageProcessor.processPollAnswer(userId, firstName, diasSeleccionados);
                if (response != null && !response.isEmpty()) {
                    sendMessage(userId, response);
                }
            } catch (Exception e) {
                logger.severe("❌ Error procesando respuesta de poll: " + e.getMessage());
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
                
                // Verificar si necesita enviar una poll
                if (response.contains("[ENVIAR_POLL_DIAS]")) {
                    // Remover el marcador de la respuesta
                    String cleanResponse = response.replace("[ENVIAR_POLL_DIAS]", "").trim();
                    
                    // Editar mensaje sin botones
                    editMessageWithButtons(chatId, messageId, cleanResponse, null);
                    
                    // Luego enviar selección de días (que actúa como poll)
                    sendPollDias(chatId);
                } else if (response.contains("[ENVIAR_POLL_FRANJAS:")) {
                    // Extraer el día del marcador
                    int startIndex = response.indexOf("[ENVIAR_POLL_FRANJAS:") + "[ENVIAR_POLL_FRANJAS:".length();
                    int endIndex = response.indexOf("]", startIndex);
                    String dia = response.substring(startIndex, endIndex);
                    
                    // Remover el marcador de la respuesta
                    String cleanResponse = response.replaceAll("\\[ENVIAR_POLL_FRANJAS:[^\\]]+\\]", "").trim();
                    
                    // Editar mensaje
                    editMessageWithButtons(chatId, messageId, cleanResponse, null);
                    
                    // Luego enviar poll de franjas horarias
                    sendPollFranjasHorarias(chatId, dia);
                } else if (response.contains("[MOSTRAR_BOTONES_CONFIRMACION]")) {
                    // Remover el marcador
                    String cleanResponse = response.replace("[MOSTRAR_BOTONES_CONFIRMACION]", "").trim();
                    
                    // Editar mensaje con botones de confirmación final
                    editMessageWithButtons(chatId, messageId, cleanResponse, createConfirmationButtons());
                } else {
                    // Determinar qué botones mostrar según la respuesta
                    org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup buttons = null;
                    if (response.contains("Modificar disponibilidad")) {
                        buttons = messageProcessor.getDiasButtons();
                    } else {
                        buttons = messageProcessor.getMenuButtons(chatId);
                    }
                    
                    // Editar el mensaje con la nueva respuesta y botones
                    editMessageWithButtons(chatId, messageId, response, buttons);
                }
                
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
    
    /**
     * Envía poll real para seleccionar días de la semana
     */
    public void sendPollDias(Long chatId) {
        SendPoll poll = new SendPoll();
        poll.setChatId(chatId.toString());
        poll.setQuestion("📅 ¿Qué días quieres modificar tu disponibilidad?");
        
        // Opciones de días
        List<String> options = Arrays.asList("Jueves", "Viernes", "Sábado", "Domingo");
        poll.setOptions(options);
        
        // Permitir múltiples respuestas
        poll.setAllowMultipleAnswers(true);
        
        // Poll no anónimo
        poll.setIsAnonymous(false);
        
        try {
            execute(poll);
            
            // Enviar botón de confirmación separado
            org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard = 
                new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
            
            java.util.List<java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> rows = 
                new java.util.ArrayList<>();
            
            java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row = 
                new java.util.ArrayList<>();
            
            org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton confirmBtn = 
                new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
            confirmBtn.setText("✅ Confirmar días seleccionados");
            confirmBtn.setCallbackData("confirmar_dias_poll");
            row.add(confirmBtn);
            
            rows.add(row);
            keyboard.setKeyboard(rows);
            
            sendMessageWithButtons(chatId, "⬆️ Selecciona los días en la encuesta de arriba y luego presiona confirmar:", keyboard);
            
            logger.info("✅ Poll de días enviado a " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando poll de días: " + e.getMessage());
            sendMessage(chatId, "❌ Error enviando encuesta. Intenta de nuevo.");
        }
    }
    
    /**
     * Envía poll para seleccionar franjas horarias de un día específico
     */
    public void sendPollFranjasHorarias(Long chatId, String dia) {
        SendPoll poll = new SendPoll();
        poll.setChatId(chatId.toString());
        poll.setQuestion("⏰ ¿En qué franjas horarias estás disponible el " + dia + "?");
        
        // Opciones de franjas horarias de 2 horas cada una
        List<String> options = Arrays.asList(
            "08:00 - 10:00",
            "10:00 - 12:00",
            "12:00 - 14:00", 
            "14:00 - 16:00",
            "16:00 - 18:00",
            "18:00 - 20:00"
        );
        poll.setOptions(options);
        
        // Permitir múltiples respuestas
        poll.setAllowMultipleAnswers(true);
        
        // Poll no anónimo
        poll.setIsAnonymous(false);
        
        try {
            execute(poll);
            
            // Enviar botón de confirmación separado
            org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard = 
                new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
            
            java.util.List<java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> rows = 
                new java.util.ArrayList<>();
            
            java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row = 
                new java.util.ArrayList<>();
            
            org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton confirmBtn = 
                new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
            confirmBtn.setText("✅ Confirmar horarios para " + dia);
            
            // Normalizar día para callback data (sin acentos)
            String diaCallback = dia.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
            confirmBtn.setCallbackData("confirmar_horarios_" + diaCallback);
            row.add(confirmBtn);
            
            rows.add(row);
            keyboard.setKeyboard(rows);
            
            sendMessageWithButtons(chatId, "⬆️ Selecciona las franjas horarias en la encuesta de arriba y luego presiona confirmar:", keyboard);
            
            logger.info("✅ Poll de franjas horarias enviado para " + dia + " a " + chatId);
        } catch (TelegramApiException e) {
            logger.severe("❌ Error enviando poll de franjas horarias: " + e.getMessage());
            sendMessage(chatId, "❌ Error enviando encuesta de horarios. Intenta de nuevo.");
        }
    }
    
    /**
     * Crea botones de confirmación final para disponibilidad
     */
    private org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup createConfirmationButtons() {
        org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
        
        java.util.List<java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> rows = 
            new java.util.ArrayList<>();
        
        // Fila 1: Confirmar y Cancelar
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton confirmBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        confirmBtn.setText("✅ Confirmar");
        confirmBtn.setCallbackData("confirmar_disponibilidad_final");
        row.add(confirmBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton cancelBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        cancelBtn.setText("❌ Cancelar");
        cancelBtn.setCallbackData("cancelar_modificacion_disponibilidad");
        row.add(cancelBtn);
        
        rows.add(row);
        keyboard.setKeyboard(rows);
        
        return keyboard;
    }
}
