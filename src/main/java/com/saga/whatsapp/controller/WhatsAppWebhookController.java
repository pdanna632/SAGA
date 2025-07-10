package com.saga.whatsapp.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saga.whatsapp.config.WhatsAppConfig;
import com.saga.whatsapp.model.WhatsAppWebhookPayload;
import com.saga.whatsapp.service.MessageProcessor;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {
    
    private static final Logger logger = Logger.getLogger(WhatsAppWebhookController.class.getName());
    
    @Autowired
    private WhatsAppConfig config;
    
    @Autowired
    private MessageProcessor messageProcessor;
    
    /**
     * Endpoint para verificación del webhook (GET)
     * WhatsApp envía este request para verificar el webhook
     */
    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String verifyToken) {
        
        logger.info("🔍 Verificando webhook...");
        logger.info("Mode: " + mode);
        logger.info("Verify Token recibido: " + verifyToken);
        logger.info("Challenge: " + challenge);
        
        // Verificar que el token coincide
        if ("subscribe".equals(mode) && config.getVerifyToken().equals(verifyToken)) {
            logger.info("✅ Webhook verificado exitosamente!");
            return ResponseEntity.ok(challenge);
        } else {
            logger.warning("❌ Token de verificación incorrecto");
            return ResponseEntity.status(403).body("Token de verificación incorrecto");
        }
    }
    
    /**
     * Endpoint para recibir mensajes (POST)
     * WhatsApp envía los mensajes a este endpoint
     */
    @PostMapping("/whatsapp")
    public ResponseEntity<String> receiveMessage(@RequestBody WhatsAppWebhookPayload payload) {
        try {
            logger.info("📨 Webhook recibido de WhatsApp");
            
            // Log del payload para debugging
            if (payload.getEntry() != null && !payload.getEntry().isEmpty()) {
                logger.info("📊 Entries recibidas: " + payload.getEntry().size());
            }
            
            // Procesar el mensaje
            messageProcessor.processIncomingMessage(payload);
            
            // WhatsApp espera una respuesta 200 OK
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            logger.severe("❌ Error procesando webhook: " + e.getMessage());
            e.printStackTrace();
            
            // Aún así retornar 200 para evitar que WhatsApp reintente
            return ResponseEntity.ok("ERROR");
        }
    }
    
    /**
     * Endpoint de salud para verificar que el servicio está activo
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("🟢 WhatsApp Bot SAGA funcionando correctamente");
    }
    
    /**
     * Endpoint para obtener información de configuración (sin datos sensibles)
     */
    @GetMapping("/info")
    public ResponseEntity<String> info() {
        StringBuilder info = new StringBuilder();
        info.append("🏆 Bot WhatsApp SAGA\n");
        info.append("📱 Phone Number ID: ").append(config.getPhoneNumberId()).append("\n");
        info.append("🆔 App ID: ").append(config.getAppId()).append("\n");
        info.append("📡 API Version: ").append(config.getApiVersion()).append("\n");
        info.append("🔗 Webhook URL: ").append(config.getWebhookUrl()).append("\n");
        info.append("✅ Estado: Activo");
        
        return ResponseEntity.ok(info.toString());
    }
}
