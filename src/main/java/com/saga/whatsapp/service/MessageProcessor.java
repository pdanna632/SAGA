package com.saga.whatsapp.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.utils.ExcelArbitroWriter;
import com.saga.whatsapp.model.WhatsAppWebhookPayload;

@Service
public class MessageProcessor {
    
    private static final Logger logger = Logger.getLogger(MessageProcessor.class.getName());
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    /**
     * Procesa los mensajes recibidos desde WhatsApp
     */
    public void processIncomingMessage(WhatsAppWebhookPayload payload) {
        try {
            if (payload.getEntry() == null || payload.getEntry().isEmpty()) {
                return;
            }
            
            for (WhatsAppWebhookPayload.Entry entry : payload.getEntry()) {
                if (entry.getChanges() == null) continue;
                
                for (WhatsAppWebhookPayload.Change change : entry.getChanges()) {
                    if (!"messages".equals(change.getField())) continue;
                    
                    WhatsAppWebhookPayload.Value value = change.getValue();
                    if (value.getMessages() == null) continue;
                    
                    for (WhatsAppWebhookPayload.Message message : value.getMessages()) {
                        processMessage(message, value);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error procesando mensaje de WhatsApp: " + e.getMessage());
        }
    }
    
    /**
     * Procesa un mensaje individual
     */
    private void processMessage(WhatsAppWebhookPayload.Message message, WhatsAppWebhookPayload.Value value) {
        String fromNumber = message.getFrom();
        String messageId = message.getId();
        String messageText = message.getText() != null ? message.getText().getBody() : "";
        
        logger.info("📥 Mensaje recibido de: " + fromNumber);
        logger.info("📝 Contenido: " + messageText);
        
        // Marcar como leído
        whatsAppService.markAsRead(messageId);
        
        // Buscar árbitro por número de teléfono
        Arbitro arbitro = buscarArbitroPorTelefono(fromNumber);
        String nombreArbitro = arbitro != null ? arbitro.getNombre() : null;
        
        // Si es la primera vez que escribe, enviar bienvenida
        if (esPrimerMensaje(messageText)) {
            whatsAppService.sendWelcomeMessage(fromNumber, nombreArbitro);
            return;
        }
        
        // Procesar comando específico
        String respuesta = procesarComando(messageText, arbitro, fromNumber);
        
        if (respuesta != null && !respuesta.isEmpty()) {
            whatsAppService.sendTextMessage(fromNumber, respuesta);
        }
    }
    
    /**
     * Busca un árbitro por su número de teléfono
     */
    private Arbitro buscarArbitroPorTelefono(String numero) {
        try {
            String rutaArchivo = "src/main/resources/data/Arbitros.xlsx";
            return ExcelArbitroWriter.buscarPorTelefono(rutaArchivo, numero);
        } catch (Exception e) {
            logger.warning("Error buscando árbitro: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Determina si es un mensaje de primer contacto
     */
    private boolean esPrimerMensaje(String texto) {
        if (texto == null) return true;
        
        String textoLower = texto.toLowerCase().trim();
        return textoLower.equals("hola") || 
               textoLower.equals("hi") || 
               textoLower.equals("hello") ||
               textoLower.equals("buenas") ||
               textoLower.equals("buenos dias") ||
               textoLower.equals("buenas tardes") ||
               textoLower.equals("buenas noches");
    }
    
    /**
     * Procesa comandos específicos del usuario
     */
    private String procesarComando(String texto, Arbitro arbitro, String numeroTelefono) {
        if (texto == null || texto.trim().isEmpty()) {
            return "🤔 No entendí tu mensaje. Escribe 'ayuda' para ver los comandos disponibles.";
        }
        
        String textoLower = texto.toLowerCase().trim();
        
        // Comandos de ayuda
        if (textoLower.contains("ayuda") || textoLower.contains("help") || textoLower.contains("comandos")) {
            return generarMensajeAyuda();
        }
        
        // Información del árbitro
        if (textoLower.contains("mi info") || textoLower.contains("mis datos") || textoLower.contains("perfil")) {
            return generarInfoArbitro(arbitro, numeroTelefono);
        }
        
        // Próximos partidos
        if (textoLower.contains("partidos") || textoLower.contains("partido") || textoLower.contains("asignaciones")) {
            return "⚽ Funcionalidad de partidos en desarrollo.\n" +
                   "Pronto podrás consultar tus asignaciones aquí.";
        }
        
        // Disponibilidades
        if (textoLower.contains("disponibilidad") || textoLower.contains("disponible")) {
            return "📅 Funcionalidad de disponibilidades en desarrollo.\n" +
                   "Pronto podrás gestionar tu disponibilidad aquí.";
        }
        
        // Contactos
        if (textoLower.contains("contacto") || textoLower.contains("teléfono") || textoLower.contains("telefono")) {
            return "📞 Funcionalidad de contactos en desarrollo.\n" +
                   "Pronto podrás consultar información de otros árbitros.";
        }
        
        // Respuesta por defecto
        return "🤔 No entendí tu consulta.\n\n" +
               "Escribe 'ayuda' para ver los comandos disponibles o " +
               "describe lo que necesitas de manera más específica.";
    }
    
    /**
     * Genera el mensaje de ayuda con comandos disponibles
     */
    private String generarMensajeAyuda() {
        return """
               🏆 **COMANDOS DISPONIBLES**
               
               📋 **Información personal:**
               • 'mi info' - Ver tus datos como árbitro
               
               ⚽ **Partidos:**
               • 'partidos' - Ver tus próximos partidos
               
               📅 **Disponibilidad:**
               • 'disponibilidad' - Gestionar tu disponibilidad
               
               📞 **Contactos:**
               • 'contacto [nombre]' - Buscar contacto de árbitro
               
               ❓ **Ayuda:**
               • 'ayuda' - Ver este mensaje
               
               💡 También puedes escribir de forma natural, ¡entiendo tu consulta!
               """;
    }
    
    /**
     * Genera información del árbitro
     */
    private String generarInfoArbitro(Arbitro arbitro, String numeroTelefono) {
        if (arbitro == null) {
            return "❌ No encontré tu información en la base de datos.\n\n" +
                   "Tu número registrado es: " + numeroTelefono + "\n" +
                   "Por favor contacta al administrador para registrarte.";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("👤 **TU INFORMACIÓN**\n\n");
        info.append("📝 **Nombre:** ").append(arbitro.getNombre()).append("\n");
        info.append("🆔 **Cédula:** ").append(arbitro.getCedula()).append("\n");
        info.append("📱 **Teléfono:** ").append(arbitro.getTelefono()).append("\n");
        info.append("🏆 **Categoría:** ").append(arbitro.getCategoria()).append("\n");
        info.append("✅ **Activo:** ").append(arbitro.isActivo() ? "Sí" : "No").append("\n");
        
        info.append("\n💡 Escribe 'partidos' para ver tus próximas asignaciones.");
        
        return info.toString();
    }
}
