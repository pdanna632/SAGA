package com.saga.telegram.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.telegram.service.TelegramAuthService.AuthResult;

@Service
public class TelegramMessageProcessor {
    
    private static final Logger logger = Logger.getLogger(TelegramMessageProcessor.class.getName());
    
    @Autowired
    private TelegramAuthService authService;
    
    /**
     * Procesa un mensaje de texto recibido
     */
    public String processMessage(String messageText, Long chatId, String userName, String userHandle, String phoneNumber) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "🤔 No entendí tu mensaje. Escribe /ayuda para ver los comandos disponibles.";
        }
        
        String chatIdStr = chatId.toString();
        String text = messageText.toLowerCase().trim();
        
        // Verificar si el usuario está esperando ingresar cédula
        if (authService.isEsperandoCedula(chatIdStr)) {
            return procesarVerificacionCedula(chatIdStr, messageText.trim());
        }
        
        // Comandos de saludo - aquí aplicamos la nueva lógica de autenticación
        if (text.equals("/start") || text.equals("hola") || text.equals("hi") || text.equals("buenas")) {
            return procesarSaludo(chatIdStr, userName, phoneNumber);
        }
        
        // Verificar si el usuario está autenticado para otros comandos
        if (!authService.isUsuarioAutenticado(chatIdStr)) {
            return """
                   🔒 *No autenticado*
                   
                   Para usar el bot, primero debes autenticarte.
                   
                   Escribe *hola* para comenzar el proceso de autenticación.
                   """;
        }
        
        // Usuario autenticado - procesar comandos
        Arbitro arbitro = authService.getArbitroAutenticado(chatIdStr);
        
        if (text.equals("/info") || text.contains("informacion") || text.contains("información") || text.contains("datos")) {
            return generateUserInfo(arbitro);
        }
        
        if (text.equals("/ayuda") || text.equals("/help") || text.contains("ayuda") || text.contains("help")) {
            return generateHelpMessage(arbitro);
        }
        
        if (text.contains("partidos") || text.contains("partido")) {
            return generatePartidosMessage(arbitro);
        }
        
        if (text.contains("disponibilidad") || text.contains("disponible")) {
            return generateDisponibilidadMessage(arbitro);
        }
        
        if (text.contains("contacto") || text.contains("contactos")) {
            return generateContactosMessage();
        }
        
        if (text.equals("/logout") || text.equals("salir")) {
            authService.cerrarSesion(chatIdStr);
            return """
                   👋 *Sesión Cerrada*
                   
                   Has cerrado sesión exitosamente.
                   
                   Para volver a usar el bot, escribe *hola* para autenticarte nuevamente.
                   """;
        }
        
        // Comando no reconocido
        return String.format("""
               🤔 No entendí el comando "*%s*"
               
               Escribe `/ayuda` para ver los comandos disponibles.
               
               💡 *También puedes escribir de forma natural:*
               • "¿Cuáles son mis partidos?"
               • "Ver mi información"
               • "Contactos de árbitros"
               """, messageText);
    }
    
    /**
     * Procesa el contacto compartido por el usuario
     */
    public String processContact(String phoneNumber, Long chatId, String firstName) {
        String chatIdStr = chatId.toString();
        logger.info("Procesando contacto compartido para chat ID: " + chatIdStr);
        
        AuthResult result = authService.autenticarPorTelefono(chatIdStr, phoneNumber);
        
        switch (result.getStatus()) {
            case AUTENTICADO:
                return generateWelcomeMessageAuthenticated(result.getArbitro());
                
            case NUMERO_NO_ENCONTRADO:
                // Iniciar proceso de verificación por cédula como backup
                authService.iniciarVerificacionPorCedula(chatIdStr);
                return String.format("""
                       📱 *Número no encontrado*
                       
                       Tu número %s no está registrado en nuestro sistema.
                       
                       🔐 **Como alternativa, puedes autenticarte con tu cédula:**
                       
                       ✏️ Por favor, ingresa tu *número de cédula*:
                       """, phoneNumber);
                
            case ERROR:
                return "❌ Error al validar el número de teléfono. Intenta de nuevo.";
                
            default:
                return "❌ Error de autenticación. Contacta al administrador.";
        }
    }
    
    /**
     * Procesa el saludo inicial y maneja la autenticación
     */
    private String procesarSaludo(String chatId, String userName, String phoneNumber) {
        // Verificar si ya está autenticado
        if (authService.isUsuarioAutenticado(chatId)) {
            Arbitro arbitro = authService.getArbitroAutenticado(chatId);
            return String.format("""
                   👋 *¡Hola de nuevo, %s!*
                   
                   Ya estás autenticado en el sistema SAGA.
                   
                   ¿En qué puedo ayudarte hoy?
                   
                   📋 /info - Ver tu información
                   ⚽ /partidos - Tus próximos partidos  
                   📅 /disponibilidad - Gestionar disponibilidad
                   📞 /contactos - Directorio de árbitros
                   ❓ /ayuda - Ver todos los comandos
                   """, arbitro.getNombre().split(" ")[0]);
        }
        
        // Solicitar cédula directamente (sin número de teléfono)
        authService.iniciarVerificacionPorCedula(chatId);
        return requestCedulaForAuth(userName);
    }
    
    /**
     * Solicita al usuario que ingrese su cédula para autenticación
     */
    private String requestCedulaForAuth(String firstName) {
        return String.format("""
                Hola *%s* 👋
                
                Bienvenido al sistema SAGA de gestión arbitral.
                
                Para acceder al sistema, necesito verificar tu identidad.
                
                ✏️ Por favor, ingresa tu *número de cédula* (solo números):
                
                🔒 *Nota:* Tu cédula debe estar registrada en nuestro sistema de árbitros.
                """, firstName);
    }
    
    /**
     * Procesa la verificación por cédula
     */
    private String procesarVerificacionCedula(String chatId, String cedula) {
        AuthResult result = authService.verificarPorCedula(chatId, cedula);
        
        switch (result.getStatus()) {
            case AUTENTICADO:
                return generateWelcomeMessageAuthenticated(result.getArbitro());
                
            case CEDULA_NO_ENCONTRADA:
                return String.format("""
                       ❌ *Cédula No Encontrada*
                       
                       La cédula *%s* no está registrada en nuestro sistema.
                       
                       Verifica el número e intenta de nuevo, o contacta al administrador.
                       
                       💡 Puedes intentar con otra cédula o escribir *hola* para reiniciar.
                       """, cedula);
                
            default:
                return """
                       ❌ *Error de Verificación*
                       
                       Ocurrió un error al verificar tu cédula. 
                       
                       Intenta de nuevo o contacta al administrador.
                       """;
        }
    }
    
    /**
     * Genera mensaje de bienvenida para usuario autenticado
     */
    private String generateWelcomeMessageAuthenticated(Arbitro arbitro) {
        return String.format("""
               🏆 *¡Hola %s!* ✅
               
               **Bienvenido al sistema SAGA**
               📋 *Nombre:* %s
               🆔 *Cédula:* %s  
               📱 *Teléfono:* %s
               🏅 *Categoría:* %s
               ✅ *Estado:* %s
               
               **¿En qué puedo ayudarte?**
               
               📋 /info - Ver tu información completa
               ⚽ /partidos - Tus próximos partidos  
               📅 /disponibilidad - Gestionar disponibilidad
               📞 /contactos - Directorio de árbitros
               ❓ /ayuda - Ver todos los comandos
               🚪 /logout - Cerrar sesión
               """, 
               arbitro.getNombre().split(" ")[0],
               arbitro.getNombre(),
               arbitro.getCedula(),
               arbitro.getTelefono(),
               arbitro.getCategoria(),
               arbitro.isActivo() ? "Activo" : "Inactivo");
    }
    
    /**
     * Genera mensaje de ayuda personalizado
     */
    private String generateHelpMessage(Arbitro arbitro) {
        return String.format("""
               🏆 *COMANDOS DISPONIBLES* - %s
               
               📋 *Información personal:*
               • `/info` - Ver tus datos completos
               
               ⚽ *Partidos:*
               • `partidos` - Ver tus próximos partidos
               
               📅 *Disponibilidad:*
               • `disponibilidad` - Gestionar tu disponibilidad
               
               📞 *Contactos:*
               • `contactos` - Buscar contacto de árbitro
               
               ❓ *Sistema:*
               • `/ayuda` - Ver este mensaje
               • `/logout` - Cerrar sesión
               
               💡 *También puedes escribir de forma natural*
               🤖 *Ejemplo:* "¿Cuáles son mis partidos de esta semana?"
               """, arbitro.getNombre().split(" ")[0]);
    }
    
    /**
     * Genera información del usuario autenticado
     */
    private String generateUserInfo(Arbitro arbitro) {
        return String.format("""
               👤 *Información Completa*
               
               📝 *Nombre:* %s
               🆔 *Cédula:* %s  
               📱 *Teléfono:* %s
               🏅 *Categoría:* %s
               ✅ *Estado:* %s
               🤖 *Vinculado a Telegram:* ✅
               
               💡 *Nota:* Tu información está sincronizada con el sistema SAGA.
               """, 
               arbitro.getNombre(),
               arbitro.getCedula(),
               arbitro.getTelefono(),
               arbitro.getCategoria(),
               arbitro.isActivo() ? "Activo" : "Inactivo");
    }
    
    private String generatePartidosMessage(Arbitro arbitro) {
        return String.format("""
               ⚽ *Mis Próximos Partidos* - %s
               
               Funcionalidad en desarrollo.
               Aquí podrás ver:
               • Partidos asignados
               • Fechas y horarios
               • Equipos participantes
               • Ubicación de canchas
               """, arbitro.getNombre().split(" ")[0]);
    }
    
    private String generateDisponibilidadMessage(Arbitro arbitro) {
        return String.format("""
               📅 *Gestión de Disponibilidad* - %s
               
               Funcionalidad en desarrollo.
               Aquí podrás:
               • Marcar tu disponibilidad
               • Ver calendario de fechas
               • Actualizar horarios libres
               """, arbitro.getNombre().split(" ")[0]);
    }
    
    private String generateContactosMessage() {
        return """
               📞 *Directorio de Contactos*
               
               Funcionalidad en desarrollo.
               Aquí podrás:
               • Buscar otros árbitros
               • Ver información de contacto
               • Acceder a directorio completo
               """;
    }
}
