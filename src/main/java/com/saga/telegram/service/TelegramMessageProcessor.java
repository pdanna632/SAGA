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
            return """
                   🤔 ¡Hola! Soy **SAGA**, tu asistente de gestión arbitral.
                   
                   Parece que no recibí ningún mensaje. ¿Podrías intentar de nuevo?
                   
                   💡 Escribe `/ayuda` para ver todos los comandos disponibles.
                   """;
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
                   🔒 ¡Hola! Soy **SAGA**, tu asistente de gestión arbitral.
                   
                   Para poder ayudarte con tus consultas, primero necesito verificar tu identidad.
                   
                   Escribe *hola* para comenzar el proceso de autenticación.
                   
                   💡 Solo árbitros registrados pueden acceder al sistema.
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
        
        if (text.equals("/logout") || text.equals("salir")) {
            authService.cerrarSesion(chatIdStr);
            return """
                   👋 ¡Hasta luego! Soy **SAGA**, tu asistente de gestión arbitral.
                   
                   Tu sesión ha sido cerrada exitosamente y de forma segura.
                   
                   🔐 **Seguridad:** Todos tus datos están protegidos.
                   
                   🔄 **Para volver a acceder:** Escribe *hola* cuando quieras autenticarte nuevamente.
                   
                   ¡Que tengas un excelente día! 🌟
                   """;
        }
        
        // Comando no reconocido
        return String.format("""
               🤔 ¡Hola! Soy **SAGA**, tu asistente de gestión arbitral.
               
               No he podido entender tu solicitud "*%s*". Pero no te preocupes, estoy aquí para ayudarte.
               
               💡 **Sugerencias:**
               • Escribe `/ayuda` para ver todos los comandos disponibles
               
               🤖 ¿Te puedo asistir con algo específico?
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
                       📱 ¡Hola! Soy **SAGA**, tu asistente de gestión arbitral.
                       
                       No he podido encontrar tu número %s en nuestro sistema de árbitros registrados.
                       
                       🔐 **No hay problema:** Como alternativa, puedes autenticarte con tu cédula.
                       
                       ✏️ Por favor, compárteme tu *número de cédula*:
                       
                       💡 Solo árbitros registrados pueden acceder al sistema.
                       """, phoneNumber);
                
            case ERROR:
                return """
                       ❌ ¡Ups! Soy **SAGA** y he encontrado un problema técnico.
                       
                       Ha ocurrido un error al validar tu número de teléfono.
                       
                       🔄 Por favor, intenta de nuevo o escribe *hola* para reiniciar.
                       """;
                
            default:
                return """
                       ❌ ¡Hola! Soy **SAGA** y ha ocurrido un error inesperado.
                       
                       Por favor, contacta al administrador del sistema para recibir asistencia.
                       
                       💡 También puedes intentar escribir *hola* para reiniciar el proceso.
                       """;
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
                   👋 ¡Hola de nuevo, %s! Soy **SAGA**, tu asistente de gestión arbitral.
                   
                   Me alegra verte otra vez. Estoy aquí para ayudarte con cualquier consulta sobre tus actividades arbitrales.
                   
                   ¿Con qué te puedo asistir hoy?
                   
                   📋 `/info` - Consultar tu información personal
                   ⚽ `/partidos` - Ver tus próximos partidos asignados
                   📅 `/disponibilidad` - Gestionar tu disponibilidad
                   ❓ `/ayuda` - Ver todos los comandos disponibles
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
                👋 ¡Hola %s! Soy **SAGA**, tu asistente de gestión arbitral.
                
                Me complace conocerte y estoy aquí para ayudarte con todo lo relacionado a tus actividades como árbitro.
                
                Para comenzar, necesito verificar tu identidad en nuestro sistema.
                
                ✏️ Por favor, compárteme tu *número de cédula* (solo números):
                
                🔒 *Nota:* Solo árbitros registrados en nuestro sistema pueden acceder.
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
                       ❌ ¡Hola! Soy **SAGA**, tu asistente de gestión arbitral.
                       
                       No he podido encontrar la cédula *%s* en nuestro sistema de árbitros registrados.
                       
                       🔍 **Verificaciones sugeridas:**
                       • Confirma que el número esté correcto
                       • Asegúrate de estar registrado como árbitro
                       • Contacta al administrador si el problema persiste
                       
                       � Puedes intentar con otra cédula o escribir *hola* para reiniciar.
                       
                       🆘 ¿Necesitas ayuda? No dudes en contactar al administrador.
                       """, cedula);
                
            default:
                return """
                       ❌ ¡Ups! Soy **SAGA** y ha ocurrido un error técnico.
                       
                       Ha habido un problema al verificar tu cédula.
                       
                       🔄 Por favor, intenta de nuevo o contacta al administrador para recibir asistencia.
                       
                       💡 También puedes escribir *hola* para reiniciar el proceso.
                       """;
        }
    }
    
    /**
     * Genera mensaje de bienvenida para usuario autenticado
     */
    private String generateWelcomeMessageAuthenticated(Arbitro arbitro) {
        return String.format("""
               � ¡Excelente, %s! Soy **SAGA**, tu asistente de gestión arbitral.
               
               Tu identidad ha sido verificada exitosamente. Me complace confirmar tus datos:
               
               📋 **Información Personal:**
               • *Nombre:* %s
               • *Cédula:* %s  
               • *Teléfono:* %s
               • *Categoría:* %s
               • *Estado:* %s
               
               🤖 **¿Con qué te puedo asistir hoy?**
               
               📋 `/info` - Consultar tu información completa
               ⚽ `/partidos` - Ver tus próximos partidos asignados
               📅 `/disponibilidad` - Gestionar tu disponibilidad
               ❓ `/ayuda` - Ver todos los comandos disponibles
               🚪 `/logout` - Cerrar sesión
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
               🤖 Hola %s, soy **SAGA**, tu asistente de gestión arbitral.
               
               Estoy aquí para ayudarte con todas tus necesidades como árbitro. Aquí tienes todos los comandos disponibles:
               
               📋 **Información Personal:**
               • `/info` - Ver tus datos completos del sistema
               
               ⚽ **Gestión de Partidos:**
               • `partidos` - Consultar tus próximos partidos asignados
               • `partido` - Información sobre partidos específicos
               
               📅 **Disponibilidad:**
               • `disponibilidad` - Gestionar tu disponibilidad semanal
               
                **Sistema:**
               • `/ayuda` - Ver este mensaje de ayuda
               • `/logout` - Cerrar tu sesión de forma segura
               
               🆘 ¿Necesitas ayuda adicional? No dudes en preguntarme cualquier cosa.
               """, arbitro.getNombre().split(" ")[0]);
    }
    
    /**
     * Genera información del usuario autenticado
     */
    private String generateUserInfo(Arbitro arbitro) {
        return String.format("""
               👤 ¡Hola %s! Aquí tienes tu información completa del sistema SAGA:
               
               📝 **Datos Personales:**
               • *Nombre Completo:* %s
               • *Número de Cédula:* %s  
               • *Teléfono de Contacto:* %s
               • *Categoría Arbitral:* %s
               • *Estado en el Sistema:* %s
               • *Telegram Vinculado:* ✅ Conectado
               
               � **Estado de Sincronización:**
               Tu información está actualizada y sincronizada con el sistema central SAGA.
               
               💡 **¿Necesitas actualizar algún dato?** Contacta con la administración para realizar cambios en tu perfil.
               
               🤖 ¿Te puedo ayudar con algo más? Escribe `/ayuda` para ver todas las opciones disponibles.
               """, 
               arbitro.getNombre().split(" ")[0],
               arbitro.getNombre(),
               arbitro.getCedula(),
               arbitro.getTelefono(),
               arbitro.getCategoria(),
               arbitro.isActivo() ? "Activo" : "Inactivo");
    }
    
    private String generatePartidosMessage(Arbitro arbitro) {
        return String.format("""
               ⚽ ¡Hola %s! Soy **SAGA**, consultando tus partidos asignados...
               
               📅 **Próximos Partidos:**
               Esta funcionalidad está en desarrollo y pronto estará disponible.
               
               🔜 **Pronto podrás ver:**
               • Calendario completo de tus partidos asignados
               • Detalles de equipos participantes
               • Horarios y fechas específicas
               • Ubicación de canchas y escenarios
               • Información de contacto de equipos
               
               🤖 Mientras tanto, ¿te puedo ayudar con algo más? Escribe `/ayuda` para ver otras opciones.
               """, arbitro.getNombre().split(" ")[0]);
    }
    
    private String generateDisponibilidadMessage(Arbitro arbitro) {
        return String.format("""
               📅 ¡Hola %s! Soy **SAGA**, gestionando tu disponibilidad...
               
               🗓️ **Gestión de Disponibilidad:**
               Esta funcionalidad está en desarrollo y pronto estará disponible.
               
               🔜 **Pronto podrás:**
               • Marcar tu disponibilidad semanal
               • Ver tu calendario de fechas libres
               • Actualizar horarios disponibles
               • Recibir notificaciones de nuevas asignaciones
               • Gestionar conflictos de horarios
               
               🤖 ¿Te puedo asistir con algo más mientras tanto? Escribe `/ayuda` para ver otras opciones.
               """, arbitro.getNombre().split(" ")[0]);
    }
}
