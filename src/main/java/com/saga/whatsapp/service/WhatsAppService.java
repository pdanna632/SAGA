package com.saga.whatsapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.whatsapp.config.WhatsAppConfig;

@Service
public class WhatsAppService {
    
    private static final Logger logger = Logger.getLogger(WhatsAppService.class.getName());
    
    @Autowired
    private WhatsAppConfig config;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Envía un mensaje de texto a un número de WhatsApp
     */
    public boolean sendTextMessage(String to, String message) {
        try {
            // Crear el payload del mensaje
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "text");
            
            Map<String, String> text = new HashMap<>();
            text.put("body", message);
            payload.put("text", text);
            
            // Convertir a JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            // Crear la petición HTTP
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(config.getMessagesEndpoint());
                
                // Headers
                request.setHeader("Authorization", "Bearer " + config.getAccessToken());
                request.setHeader("Content-Type", "application/json");
                
                // Body
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
                
                // Ejecutar la petición
                var response = httpClient.execute(request);
                int statusCode = response.getCode();
                
                if (statusCode == 200) {
                    logger.info("✅ Mensaje enviado exitosamente a: " + to);
                    return true;
                } else {
                    logger.warning("❌ Error al enviar mensaje. Status: " + statusCode);
                    return false;
                }
            }
            
        } catch (Exception e) {
            logger.severe("❌ Error al enviar mensaje de WhatsApp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Marca un mensaje como leído
     */
    public boolean markAsRead(String messageId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("status", "read");
            payload.put("message_id", messageId);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(config.getMessagesEndpoint());
                
                request.setHeader("Authorization", "Bearer " + config.getAccessToken());
                request.setHeader("Content-Type", "application/json");
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
                
                var response = httpClient.execute(request);
                return response.getCode() == 200;
            }
            
        } catch (Exception e) {
            logger.severe("❌ Error al marcar mensaje como leído: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía mensaje de bienvenida
     */
    public boolean sendWelcomeMessage(String to, String name) {
        String welcomeMessage = String.format(
            "🏆 ¡Hola %s! Bienvenido al bot de SAGA.\n\n" +
            "Soy tu asistente para consultas sobre arbitraje. " +
            "Puedes preguntarme sobre:\n\n" +
            "📋 Tu información como árbitro\n" +
            "⚽ Partidos asignados\n" +
            "📅 Disponibilidades\n" +
            "📞 Contacto de otros árbitros\n\n" +
            "¿En qué puedo ayudarte hoy?", 
            name != null ? name : "árbitro"
        );
        
        return sendTextMessage(to, welcomeMessage);
    }
}
