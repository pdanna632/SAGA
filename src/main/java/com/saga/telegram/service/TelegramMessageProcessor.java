package com.saga.telegram.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.utils.ExcelArbitroWriter;

@Service
public class TelegramMessageProcessor {
    
    private static final Logger logger = Logger.getLogger(TelegramMessageProcessor.class.getName());
    
    /**
     * Procesa un mensaje de texto recibido
     */
    public String processMessage(String messageText, Long chatId, String userName, String userHandle) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "🤔 No entendí tu mensaje. Escribe /ayuda para ver los comandos disponibles.";
        }
        
        String text = messageText.toLowerCase().trim();
        
        // Comandos básicos
        if (text.equals("/start") || text.equals("hola") || text.equals("hi") || text.equals("buenas")) {
            return generateWelcomeMessage(userName, chatId);
        }
        
        if (text.equals("/ayuda") || text.equals("ayuda") || text.equals("help")) {
            return generateHelpMessage();
        }
        
        if (text.equals("/info") || text.contains("mi info") || text.contains("mis datos")) {
            return generateUserInfo(chatId, userName);
        }
        
        if (text.contains("partidos") || text.contains("partido")) {
            return "⚽ *Mis Partidos*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás consultar tus partidos asignados aquí.";
        }
        
        if (text.contains("disponibilidad") || text.contains("disponible")) {
            return "📅 *Disponibilidad*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás gestionar tu disponibilidad aquí.";
        }
        
        if (text.contains("contacto") || text.contains("contactos")) {
            return "📞 *Contactos*\n\nFuncionalidad en desarrollo.\n" +
                   "Pronto podrás consultar contactos de otros árbitros.";
        }
        
        // Respuesta por defecto
        return "🤔 No entendí tu consulta.\n\n" +
               "Escribe /ayuda para ver los comandos disponibles o " +
               "usa el menú de botones para navegar.";
    }
    
    /**
     * Procesa callbacks de botones inline
     */
    public String processCallbackQuery(String callbackData, Long chatId, String userName) {
        switch (callbackData) {
            case "mi_info":
                return generateUserInfo(chatId, userName);
            case "mis_partidos":
                return "⚽ *Mis Próximos Partidos*\n\n" +
                       "Funcionalidad en desarrollo.\n" +
                       "Aquí podrás ver:\n" +
                       "• Partidos asignados\n" +
                       "• Fechas y horarios\n" +
                       "• Equipos participantes\n" +
                       "• Ubicación de canchas";
            case "disponibilidad":
                return "📅 *Gestión de Disponibilidad*\n\n" +
                       "Funcionalidad en desarrollo.\n" +
                       "Aquí podrás:\n" +
                       "• Marcar tu disponibilidad\n" +
                       "• Ver calendario de fechas\n" +
                       "• Actualizar horarios libres";
            case "contactos":
                return "📞 *Directorio de Contactos*\n\n" +
                       "Funcionalidad en desarrollo.\n" +
                       "Aquí podrás:\n" +
                       "• Buscar otros árbitros\n" +
                       "• Ver información de contacto\n" +
                       "• Acceder a directorio completo";
            case "ayuda":
                return generateHelpMessage();
            default:
                return "🤔 Opción no reconocida.";
        }
    }
    
    /**
     * Genera mensaje de bienvenida
     */
    private String generateWelcomeMessage(String userName, Long chatId) {
        // Intentar buscar árbitro por chat ID o username
        // Por ahora solo mensaje genérico
        return String.format(
            "🏆 *¡Hola %s!*\n\n" +
            "Bienvenido al bot de SAGA (Sistema Automatizado de Gestión Arbitral).\n\n" +
            "Soy tu asistente para consultas sobre arbitraje.\n\n" +
            "*¿En qué puedo ayudarte?*\n\n" +
            "• 📋 Consultar tu información\n" +
            "• ⚽ Ver tus partidos\n" +
            "• 📅 Gestionar disponibilidad\n" +
            "• 📞 Buscar contactos\n\n" +
            "Usa los botones del menú o escribe /ayuda para más información.",
            userName
        );
    }
    
    /**
     * Genera mensaje de ayuda
     */
    private String generateHelpMessage() {
        return """
               🏆 *COMANDOS DISPONIBLES*
               
               📋 *Información personal:*
               • `/info` - Ver tus datos como árbitro
               • `mi info` - Ver tus datos
               
               ⚽ *Partidos:*
               • `partidos` - Ver tus próximos partidos
               
               📅 *Disponibilidad:*
               • `disponibilidad` - Gestionar tu disponibilidad
               
               📞 *Contactos:*
               • `contactos` - Buscar contacto de árbitro
               
               ❓ *Ayuda:*
               • `/ayuda` - Ver este mensaje
               • `/start` - Mensaje de bienvenida
               
               💡 *También puedes:*
               • Escribir de forma natural
               • Usar los botones del menú
               • Combinar comandos con texto libre
               
               🤖 *Ejemplo:* "¿Cuáles son mis partidos de esta semana?"
               """;
    }
    
    /**
     * Genera información del usuario
     */
    private String generateUserInfo(Long chatId, String userName) {
        try {
            // Buscar árbitro por chat ID (implementar en el futuro)
            // Por ahora intentamos buscar por nombre o retornamos info genérica
            
            String rutaArchivo = "src/main/resources/data/Arbitros.xlsx";
            // TODO: Implementar búsqueda por chat ID cuando se almacene
            
            return String.format(
                "👤 *Información de Usuario*\n\n" +
                "📝 *Nombre en Telegram:* %s\n" +
                "🆔 *Chat ID:* `%s`\n\n" +
                "❗ *Nota:* Para acceder a tu información completa como árbitro, " +
                "necesitamos vincular tu cuenta de Telegram con tu registro en SAGA.\n\n" +
                "Por favor, proporciona tu número de teléfono o cédula para " +
                "vincular tu cuenta automáticamente.",
                userName,
                chatId
            );
            
        } catch (Exception e) {
            logger.warning("Error obteniendo información del usuario: " + e.getMessage());
            return "❌ Error obteniendo tu información.\n\n" +
                   "Por favor, intenta más tarde o contacta al administrador.";
        }
    }
    
    /**
     * Busca un árbitro por diferentes criterios
     */
    private Arbitro buscarArbitro(String criterio) {
        try {
            String rutaArchivo = "src/main/resources/data/Arbitros.xlsx";
            
            // Intentar búsqueda por teléfono
            if (criterio.matches("\\d+")) {
                return ExcelArbitroWriter.buscarPorTelefono(rutaArchivo, criterio);
            }
            
            // TODO: Implementar búsqueda por nombre, cédula, etc.
            return null;
            
        } catch (Exception e) {
            logger.warning("Error buscando árbitro: " + e.getMessage());
            return null;
        }
    }
}
