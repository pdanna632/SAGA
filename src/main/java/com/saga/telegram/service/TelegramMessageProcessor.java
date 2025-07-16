package com.saga.telegram.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    
    @Autowired
    private DisponibilidadTelegramService disponibilidadService;
    
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
        
        // Comandos de modificación de disponibilidad
        if (text.startsWith("modificar") && (text.contains("disponibilidad") || text.contains("disponible"))) {
            return iniciarModificacionDisponibilidad(arbitro);
        }
        
        // Procesar comandos de modificación de disponibilidad en formato:
        // "dia:hora_inicio:hora_fin" (ej: "viernes:10:00:14:00")
        if (text.contains(":") && esComandoDisponibilidad(text)) {
            return procesarModificacionDisponibilidad(arbitro, text);
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
               • `disponibilidad` - Ver tu disponibilidad actual
               • `modificar disponibilidad` - Cambiar tu disponibilidad
               • `viernes:10:00:14:00` - Formato directo para modificar
               
               🔧 **Sistema:**
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
        // Cargar disponibilidades actuales
        disponibilidadService.cargarDisponibilidadesArbitro(arbitro);
        
        String disponibilidadActual = disponibilidadService.obtenerDisponibilidadTexto(arbitro);
        
        return String.format("""
               %s
               
               📝 **Para modificar tu disponibilidad:**
               
               🔧 Escribe: *modificar disponibilidad*
               
               O usa el formato directo:
               `dia:hora_inicio:hora_fin`
               
               **Ejemplos:**
               • `viernes:10:00:14:00`
               • `sabado:08:00:12:00`
               • `domingo:14:00:18:00`
               
               **Días disponibles:** jueves, viernes, sábado, domingo
               **Formato hora:** HH:mm (ejemplo: 14:30)
               
               💡 Los cambios se guardan automáticamente en Excel.
               """, disponibilidadActual);
    }
    
    /**
     * Inicia el proceso de modificación de disponibilidad
     */
    private String iniciarModificacionDisponibilidad(Arbitro arbitro) {
        // Cargar disponibilidades actuales
        disponibilidadService.cargarDisponibilidadesArbitro(arbitro);
        
        String disponibilidadActual = disponibilidadService.obtenerDisponibilidadTexto(arbitro);
        
        // Establecer estado inicial para el usuario (necesitamos el chatId desde el contexto)
        // El chatId se manejará desde el método que llama a este
        
        return String.format("""
               %s
               
               🔧 **Modificar Disponibilidad**
               
               A continuación recibirás una poll para seleccionar los días que quieres modificar.
               
               📅 **Proceso:**
               1. Selecciona los días en la poll
               2. Presiona 'Confirmar días seleccionados'
               3. Para cada día seleccionado, elige las franjas horarias en polls separadas
               4. Confirma tus cambios finales
               
               ⏰ **Franjas horarias disponibles (2 horas cada una):**
               • 08:00 - 10:00
               • 10:00 - 12:00
               • 12:00 - 14:00
               • 14:00 - 16:00
               • 16:00 - 18:00
               • 18:00 - 20:00
               
               [ENVIAR_POLL_DIAS]
               """, disponibilidadActual);
    }
    
    /**
     * Verifica si un texto es un comando de disponibilidad válido
     */
    private boolean esComandoDisponibilidad(String text) {
        String[] partes = text.split(":");
        if (partes.length != 3) {
            return false;
        }
        
        String dia = partes[0].trim();
        String horaInicio = partes[1].trim();
        String horaFin = partes[2].trim();
        
        return disponibilidadService.validarDia(dia) && 
               disponibilidadService.validarFormatoHora(horaInicio) && 
               disponibilidadService.validarFormatoHora(horaFin);
    }
    
    /**
     * Procesa la modificación de disponibilidad
     */
    private String procesarModificacionDisponibilidad(Arbitro arbitro, String comando) {
        try {
            String[] partes = comando.split(":");
            if (partes.length != 3) {
                return """
                       ❌ **Formato incorrecto**
                       
                       Usa el formato: `dia:hora_inicio:hora_fin`
                       
                       **Ejemplo:** `viernes:10:00:14:00`
                       """;
            }
            
            String diaRaw = partes[0].trim();
            String horaInicio = partes[1].trim();
            String horaFin = partes[2].trim();
            
            // Validar día
            String dia = disponibilidadService.normalizarDia(diaRaw);
            if (dia == null) {
                return String.format("""
                       ❌ **Día inválido: "%s"**
                       
                       Días válidos: jueves, viernes, sábado, domingo
                       
                       **Ejemplo:** `viernes:10:00:14:00`
                       """, diaRaw);
            }
            
            // Validar formato de horas
            if (!disponibilidadService.validarFormatoHora(horaInicio)) {
                return String.format("""
                       ❌ **Hora de inicio inválida: "%s"**
                       
                       Usa formato HH:mm (ejemplo: 14:30)
                       
                       **Ejemplo:** `viernes:10:00:14:00`
                       """, horaInicio);
            }
            
            if (!disponibilidadService.validarFormatoHora(horaFin)) {
                return String.format("""
                       ❌ **Hora de fin inválida: "%s"**
                       
                       Usa formato HH:mm (ejemplo: 14:30)
                       
                       **Ejemplo:** `viernes:10:00:14:00`
                       """, horaFin);
            }
            
            // Cargar disponibilidades actuales
            disponibilidadService.cargarDisponibilidadesArbitro(arbitro);
            
            // Intentar modificar disponibilidad
            boolean exito = disponibilidadService.modificarDisponibilidad(arbitro, dia, horaInicio, horaFin);
            
            if (!exito) {
                return """
                       ❌ **Error en la modificación**
                       
                       La hora de fin debe ser posterior a la hora de inicio.
                       
                       **Ejemplo válido:** `viernes:10:00:14:00`
                       """;
            }
            
            // Guardar en Excel
            boolean guardado = disponibilidadService.guardarDisponibilidadEnExcel(arbitro);
            
            if (!guardado) {
                return """
                       ⚠️ **Disponibilidad modificada parcialmente**
                       
                       Los cambios se aplicaron en memoria pero hubo un error al guardar en Excel.
                       
                       Contacta al administrador si el problema persiste.
                       """;
            }
            
            // Éxito total
            String disponibilidadActualizada = disponibilidadService.obtenerDisponibilidadTexto(arbitro);
            
            return String.format("""
                   ✅ **¡Disponibilidad actualizada exitosamente!**
                   
                   📝 **Cambio realizado:**
                   • **Día:** %s
                   • **Horario:** %s - %s
                   
                   💾 **Estado:** Guardado en Excel ✓
                   
                   %s
                   
                   💡 Puedes modificar otro día usando el mismo formato.
                   """, dia, horaInicio, horaFin, disponibilidadActualizada);
                   
        } catch (Exception e) {
            logger.severe("Error procesando modificación de disponibilidad: " + e.getMessage());
            return """
                   ❌ **Error inesperado**
                   
                   Ha ocurrido un error al procesar tu solicitud.
                   
                   Por favor, intenta de nuevo o contacta al administrador.
                   """;
        }
    }
    
    /**
     * Procesa callbacks de botones
     */
    public String processCallback(String callbackData, Long chatId, String firstName) {
        String chatIdStr = chatId.toString();
        
        // Verificar autenticación
        if (!authService.isUsuarioAutenticado(chatIdStr)) {
            return """
                   🔒 Tu sesión ha expirado.
                   
                   Por favor, envía *hola* para autenticarte nuevamente.
                   """;
        }
        
        Arbitro arbitro = authService.getArbitroAutenticado(chatIdStr);
        
        switch (callbackData) {
            case "info":
                return generateUserInfo(arbitro);
                
            case "ayuda":
                return generateHelpMessage(arbitro);
                
            case "partidos":
                return generatePartidosMessage(arbitro);
                
            case "disponibilidad":
                return generateDisponibilidadMessage(arbitro);
                
            case "modificar_disponibilidad":
                // Establecer estado inicial
                estadoModificacion.put(chatIdStr, "SELECCIONANDO_DIAS");
                diasSeleccionados.remove(chatIdStr);
                return iniciarModificacionDisponibilidad(arbitro);
                
            case "menu_principal":
                return String.format("""
                       🏆 ¡Hola %s! Soy **SAGA**, tu asistente de gestión arbitral.
                       
                       Selecciona una opción del menú:
                       """, arbitro.getNombre().split(" ")[0]);
                       
            case "logout":
                authService.cerrarSesion(chatIdStr);
                return """
                       👋 ¡Hasta luego! Tu sesión ha sido cerrada exitosamente.
                       
                       🔄 Para volver a acceder, escribe *hola* cuando quieras autenticarte nuevamente.
                       """;
                       
            // Callbacks para días de la semana
            case "dia_jueves":
                return iniciarSeleccionFranjas("Jueves", chatIdStr, arbitro);
            case "dia_viernes":
                return iniciarSeleccionFranjas("Viernes", chatIdStr, arbitro);
            case "dia_sabado":
                return iniciarSeleccionFranjas("Sábado", chatIdStr, arbitro);
            case "dia_domingo":
                return iniciarSeleccionFranjas("Domingo", chatIdStr, arbitro);
                
            // Callbacks para selección múltiple de días
            case "seleccionar_dia_jueves":
                return toggleDiaSeleccionado("Jueves", chatIdStr);
            case "seleccionar_dia_viernes":
                return toggleDiaSeleccionado("Viernes", chatIdStr);
            case "seleccionar_dia_sabado":
                return toggleDiaSeleccionado("Sábado", chatIdStr);
            case "seleccionar_dia_domingo":
                return toggleDiaSeleccionado("Domingo", chatIdStr);
                
            // Callbacks para confirmar selecciones de polls
            case "confirmar_dias_poll":
                return confirmarDiasYEnviarPrimeraFranja(chatIdStr, arbitro);
                
            case "confirmar_horarios_jueves":
                return confirmarHorariosYContinuar(chatIdStr, "Jueves", arbitro);
            case "confirmar_horarios_viernes":
                return confirmarHorariosYContinuar(chatIdStr, "Viernes", arbitro);
            case "confirmar_horarios_sabado":
                return confirmarHorariosYContinuar(chatIdStr, "Sábado", arbitro);
            case "confirmar_horarios_domingo":
                return confirmarHorariosYContinuar(chatIdStr, "Domingo", arbitro);
                
            case "confirmar_disponibilidad_final":
                return confirmarDisponibilidadFinal(chatIdStr, arbitro);
                
            case "cancelar_modificacion_disponibilidad":
                return cancelarModificacionCompleta(chatIdStr);
                
            default:
                // Verificar si es un comando de modificación de disponibilidad
                if (callbackData.startsWith("mod_disp:")) {
                    return procesarModificacionCallback(callbackData, arbitro);
                }
                
                return """
                       🤔 Opción no reconocida.
                       
                       Por favor, selecciona una de las opciones disponibles.
                       """;
        }
    }
    
    /**
     * Genera botones del menú según el estado del usuario
     */
    public org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup getMenuButtons(Long chatId) {
        String chatIdStr = chatId.toString();
        
        // Si no está autenticado, no mostrar botones
        if (!authService.isUsuarioAutenticado(chatIdStr)) {
            return null;
        }
        
        // Crear teclado inline
        org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
        
        java.util.List<java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> rows = 
            new java.util.ArrayList<>();
        
        // Fila 1: Info y Ayuda
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row1 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton infoBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        infoBtn.setText("👤 Mi Info");
        infoBtn.setCallbackData("info");
        row1.add(infoBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton helpBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        helpBtn.setText("❓ Ayuda");
        helpBtn.setCallbackData("ayuda");
        row1.add(helpBtn);
        
        rows.add(row1);
        
        // Fila 2: Partidos
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row2 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton partidosBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        partidosBtn.setText("⚽ Mis Partidos");
        partidosBtn.setCallbackData("partidos");
        row2.add(partidosBtn);
        
        rows.add(row2);
        
        // Fila 3: Disponibilidad
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row3 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton dispBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        dispBtn.setText("📅 Ver Disponibilidad");
        dispBtn.setCallbackData("disponibilidad");
        row3.add(dispBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton modDispBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        modDispBtn.setText("🔧 Modificar");
        modDispBtn.setCallbackData("modificar_disponibilidad");
        row3.add(modDispBtn);
        
        rows.add(row3);
        
        // Fila 4: Menú principal y Logout
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row4 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton menuBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        menuBtn.setText("🏠 Menú Principal");
        menuBtn.setCallbackData("menu_principal");
        row4.add(menuBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton logoutBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        logoutBtn.setText("🚪 Salir");
        logoutBtn.setCallbackData("logout");
        row4.add(logoutBtn);
        
        rows.add(row4);
        
        keyboard.setKeyboard(rows);
        return keyboard;
    }
    
    /**
     * Genera botones para seleccionar días de la semana
     */
    public org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup getDiasButtons() {
        org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup keyboard = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
        
        java.util.List<java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> rows = 
            new java.util.ArrayList<>();
        
        // Fila 1: Jueves y Viernes
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row1 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton jueBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        jueBtn.setText("📅 Jueves");
        jueBtn.setCallbackData("dia_jueves");
        row1.add(jueBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton vieBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        vieBtn.setText("📅 Viernes");
        vieBtn.setCallbackData("dia_viernes");
        row1.add(vieBtn);
        
        rows.add(row1);
        
        // Fila 2: Sábado y Domingo
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row2 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton sabBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        sabBtn.setText("📅 Sábado");
        sabBtn.setCallbackData("dia_sabado");
        row2.add(sabBtn);
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton domBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        domBtn.setText("📅 Domingo");
        domBtn.setCallbackData("dia_domingo");
        row2.add(domBtn);
        
        rows.add(row2);
        
        // Fila 3: Volver
        java.util.List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row3 = 
            new java.util.ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton backBtn = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        backBtn.setText("⬅️ Volver");
        backBtn.setCallbackData("menu_principal");
        row3.add(backBtn);
        
        rows.add(row3);
        
        keyboard.setKeyboard(rows);
        return keyboard;
    }
    
    /**
     * Muestra formulario para modificar disponibilidad de un día específico
     */
    private String mostrarFormularioDisponibilidad(String dia, Arbitro arbitro) {
        return String.format("""
               📅 **Modificar disponibilidad - %s**
               
               Para cambiar tu disponibilidad del %s, escribe en el siguiente formato:
               
               `%s:hora_inicio:hora_fin`
               
               **Ejemplos:**
               • `%s:09:00:13:00`
               • `%s:14:00:18:00`
               • `%s:08:00:12:00`
               
               **Instrucciones:**
               • Usa formato 24 horas (HH:mm)
               • La hora fin debe ser mayor que la hora inicio
               • Los cambios se guardan automáticamente
               
               📝 Escribe tu nueva disponibilidad:
               """, dia, dia, dia.toLowerCase(), dia, dia, dia);
    }
    
    /**
     * Procesa modificación de disponibilidad desde callback
     */
    private String procesarModificacionCallback(String callbackData, Arbitro arbitro) {
        // Formato esperado: "mod_disp:dia:hora_inicio:hora_fin"
        String[] partes = callbackData.split(":");
        if (partes.length != 4) {
            return "❌ Formato de callback inválido.";
        }
        
        String dia = partes[1];
        String horaInicio = partes[2];
        String horaFin = partes[3];
        
        return procesarModificacionDisponibilidad(arbitro, dia + ":" + horaInicio + ":" + horaFin);
    }
    
    // Map para trackear el estado de modificación de disponibilidad de cada usuario
    private final java.util.Map<String, String> estadoModificacion = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, java.util.Set<String>> franjasSeleccionadas = new java.util.concurrent.ConcurrentHashMap<>();
    
    // Estado para rastrear días seleccionados por usuario
    private final java.util.Map<String, java.util.Set<String>> diasSeleccionados = new java.util.concurrent.ConcurrentHashMap<>();
    
    /**
     * Genera las franjas horarias disponibles
     */
    private java.util.List<String> generarFranjasHorarias() {
        return java.util.Arrays.asList(
            "08:00 - 10:00",
            "10:00 - 12:00", 
            "12:00 - 14:00",
            "14:00 - 16:00",
            "16:00 - 18:00",
            "18:00 - 20:00"
        );
    }
    
    /**
     * Convierte franjas seleccionadas a horario continuo
     */
    private String[] calcularHorarioContinuo(java.util.Set<String> franjas) {
        if (franjas.isEmpty()) {
            return new String[]{"08:00", "08:00"}; // Si no hay franjas, horario vacío
        }
        
        java.util.List<String> franjasOrdenadas = new java.util.ArrayList<>(franjas);
        java.util.Collections.sort(franjasOrdenadas);
        
        String primeraFranja = franjasOrdenadas.get(0);
        String ultimaFranja = franjasOrdenadas.get(franjasOrdenadas.size() - 1);
        
        // Manejar formato "HH:mm - HH:mm" con validación
        String[] partesPrimera = primeraFranja.split(" - ");
        String[] partesUltima = ultimaFranja.split(" - ");
        
        if (partesPrimera.length < 2 || partesUltima.length < 2) {
            // Si no tiene el formato esperado, devolver horario por defecto
            return new String[]{"08:00", "18:00"};
        }
        
        String horaInicio = partesPrimera[0].trim();
        String horaFin = partesUltima[1].trim();
        
        return new String[]{horaInicio, horaFin};
    }
    
    private String iniciarSeleccionFranjas(String dia, String chatId, Arbitro arbitro) {
        // Guardar el estado de modificación
        estadoModificacion.put(chatId, dia);
        
        // Limpiar franjas anteriores si existen
        franjasSeleccionadas.put(chatId, new HashSet<>());
        
        // Crear mensaje con botones para selección de franjas horarias
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("📅 Selecciona las franjas horarias disponibles para ").append(dia).append(":\n\n");
        mensaje.append("Haz clic en los botones de las franjas en las que estarás disponible:\n\n");
        mensaje.append("🕐 08:00 - 10:00 → /franja_08_10\n");
        mensaje.append("🕐 10:00 - 12:00 → /franja_10_12\n");
        mensaje.append("🕐 12:00 - 14:00 → /franja_12_14\n");
        mensaje.append("🕐 14:00 - 16:00 → /franja_14_16\n");
        mensaje.append("🕐 16:00 - 18:00 → /franja_16_18\n");
        mensaje.append("🕐 18:00 - 20:00 → /franja_18_20\n\n");
        mensaje.append("Una vez que hayas seleccionado todas las franjas, envía:\n");
        mensaje.append("📝 /confirmar_disponibilidad - Para guardar los cambios\n");
        mensaje.append("❌ /cancelar_modificacion - Para cancelar");
        
        return mensaje.toString();
    }
    
    /**
     * Toggle para agregar/quitar franja horaria
     */
    private String toggleFranja(String chatId, String franja) {
        Set<String> franjas = franjasSeleccionadas.get(chatId);
        if (franjas == null) {
            return "❌ No hay una modificación de disponibilidad en curso. Usa /modificar_disponibilidad primero.";
        }
        
        if (franjas.contains(franja)) {
            franjas.remove(franja);
            return "➖ Franja " + franja + " removida.\n\n📋 Franjas seleccionadas: " + 
                   (franjas.isEmpty() ? "Ninguna" : String.join(", ", franjas)) + 
                   "\n\n💡 Continúa seleccionando o envía /confirmar_disponibilidad";
        } else {
            franjas.add(franja);
            return "✅ Franja " + franja + " agregada.\n\n📋 Franjas seleccionadas: " + 
                   String.join(", ", franjas) + 
                   "\n\n💡 Continúa seleccionando o envía /confirmar_disponibilidad";
        }
    }
    
    private String toggleFranjaDia(String chatId, String dia, String franja) {
        String key = chatId + "_" + dia;
        Set<String> franjas = franjasSeleccionadas.get(key);
        
        if (franjas == null) {
            return "❌ No hay modificación en curso para " + dia + ". Usa /modificar_disponibilidad primero.";
        }
        
        if (franjas.contains(franja)) {
            franjas.remove(franja);
            return "➖ " + dia + ": Franja " + franja + " removida.\n\n📋 Franjas para " + dia + ": " + 
                   (franjas.isEmpty() ? "Ninguna" : String.join(", ", franjas));
        } else {
            franjas.add(franja);
            return "✅ " + dia + ": Franja " + franja + " agregada.\n\n📋 Franjas para " + dia + ": " + 
                   String.join(", ", franjas);
        }
    }
    
    private String confirmarDisponibilidad(String chatId, Arbitro arbitro) {
        String dia = estadoModificacion.get(chatId);
        Set<String> franjas = franjasSeleccionadas.get(chatId);
        
        if (dia == null || franjas == null) {
            return "❌ No hay una modificación de disponibilidad en curso. Usa /modificar_disponibilidad primero.";
        }
        
        try {
            if (franjas.isEmpty()) {
                // El árbitro no estará disponible este día
                disponibilidadService.modificarDisponibilidad(arbitro, dia, "No disponible", "");
            } else {
                // Calcular horario continuo desde las franjas seleccionadas
                String[] horario = calcularHorarioContinuo(franjas);
                disponibilidadService.modificarDisponibilidad(arbitro, dia, horario[0], horario[1]);
            }
            
            // Limpiar estado
            estadoModificacion.remove(chatId);
            franjasSeleccionadas.remove(chatId);
            
            return "✅ Disponibilidad actualizada exitosamente para " + dia + ".\n\n" +
                   "📋 Franjas seleccionadas: " + (franjas.isEmpty() ? "No disponible" : String.join(", ", franjas)) + 
                   "\n\n💡 Puedes modificar otro día usando /modificar_disponibilidad";
            
        } catch (Exception e) {
            logger.severe("Error confirmando disponibilidad: " + e.getMessage());
            return "❌ Error al guardar la disponibilidad. Por favor, intenta nuevamente.";
        }
    }
    
    /**
     * Confirma la disponibilidad seleccionada por el usuario
     */
    private String confirmarDisponibilidadMultiple(String chatId, Arbitro arbitro) {
        StringBuilder response = new StringBuilder();
        response.append("💾 **Guardando disponibilidades...**\n\n");
        
        boolean hayErrores = false;
        int diasModificados = 0;
        
        // Buscar todas las claves que pertenecen a este usuario
        for (String key : estadoModificacion.keySet()) {
            if (key.startsWith(chatId + "_")) {
                String dia = estadoModificacion.get(key);
                Set<String> franjas = franjasSeleccionadas.get(key);
                
                if (franjas != null) {
                    try {
                        if (franjas.isEmpty()) {
                            disponibilidadService.modificarDisponibilidad(arbitro, dia, "No disponible", "");
                            response.append("✅ ").append(dia).append(": No disponible\n");
                        } else {
                            String[] horario = calcularHorarioContinuo(franjas);
                            disponibilidadService.modificarDisponibilidad(arbitro, dia, horario[0], horario[1]);
                            response.append("✅ ").append(dia).append(": ").append(horario[0]).append(" - ").append(horario[1]).append("\n");
                        }
                        diasModificados++;
                    } catch (Exception e) {
                        response.append("❌ ").append(dia).append(": Error al guardar\n");
                        hayErrores = true;
                    }
                }
            }
        }
        
        // Limpiar estado
        estadoModificacion.entrySet().removeIf(entry -> entry.getKey().startsWith(chatId + "_"));
        franjasSeleccionadas.entrySet().removeIf(entry -> entry.getKey().startsWith(chatId + "_"));
        
        response.append("\n");
        if (hayErrores) {
            response.append("⚠️ Se completó con algunos errores. Verifica tu disponibilidad.");
        } else {
            response.append("🎉 ¡Disponibilidad actualizada exitosamente! (").append(diasModificados).append(" días modificados)");
        }
        
        response.append("\n\n💡 Usa /disponibilidad para ver tus horarios actualizados.");
        
        return response.toString();
    }
    
    /**
     * Cancela la modificación de disponibilidad
     */
    private String cancelarModificacion(String chatId) {
        String dia = estadoModificacion.remove(chatId);
        franjasSeleccionadas.remove(chatId);
        
        if (dia != null) {
            return "❌ Modificación de disponibilidad para " + dia + " cancelada.\n\n" +
                   "💡 Puedes iniciar una nueva modificación con /modificar_disponibilidad";
        } else {
            return "ℹ️ No había ninguna modificación de disponibilidad en curso.";
        }
    }
    
    /**
     * Procesa respuesta de poll para selección de días o franjas horarias
     */
    public String processPollAnswer(Long chatId, String userName, List<String> selectedOptions) {
        try {
            Arbitro arbitro = authService.getArbitroAutenticado(chatId.toString());
            if (arbitro == null) {
                return "❌ Debes autenticarte primero. Envía tu contacto o cédula.";
            }
            
            String chatIdStr = chatId.toString();
            
            if (selectedOptions.isEmpty()) {
                return "❌ No seleccionaste ninguna opción. Intenta de nuevo.";
            }
            
            // Verificar si el usuario está en proceso de modificación
            String estado = estadoModificacion.get(chatIdStr);
            
            if (estado == null || estado.equals("SELECCIONANDO_DIAS")) {
                // Guardar días seleccionados para este usuario
                diasSeleccionados.put(chatIdStr, new HashSet<>(selectedOptions));
                
                StringBuilder response = new StringBuilder();
                response.append("✅ **Días seleccionados:** ").append(String.join(", ", selectedOptions)).append("\n\n");
                response.append("� Ahora presiona **'Confirmar días seleccionados'** para continuar con la selección de horarios.");
                
                // Cambiar estado para esperar confirmación
                estadoModificacion.put(chatIdStr, "ESPERANDO_CONFIRMACION_DIAS");
                
                return response.toString();
                
            } else if (estado.startsWith("SELECCIONANDO_HORARIOS_")) {
                // Extraer el día del estado
                String dia = estado.replace("SELECCIONANDO_HORARIOS_", "");
                
                // Guardar franjas seleccionadas para este día
                String key = chatIdStr + "_" + dia;
                Set<String> franjas = franjasSeleccionadas.computeIfAbsent(key, k -> new HashSet<>());
                franjas.clear();
                franjas.addAll(selectedOptions);
                
                StringBuilder response = new StringBuilder();
                response.append("✅ **Franjas horarias para ").append(dia).append(":**\n");
                response.append(String.join(", ", selectedOptions)).append("\n\n");
                response.append("🔔 Presiona **'Confirmar horarios para ").append(dia).append("'** para continuar.");
                
                return response.toString();
            }
            
            return "❌ Estado de modificación no reconocido. Usa /modificar_disponibilidad para empezar de nuevo.";
            
        } catch (Exception e) {
            logger.severe("Error procesando respuesta de poll: " + e.getMessage());
            return "❌ Error procesando tu selección. Intenta de nuevo.";
        }
    }
    
    /**
     * Toggle para agregar/quitar día seleccionado
     */
    private String toggleDiaSeleccionado(String dia, String chatId) {
        Set<String> dias = diasSeleccionados.computeIfAbsent(chatId, k -> new HashSet<>());
        
        if (dias.contains(dia)) {
            dias.remove(dia);
        } else {
            dias.add(dia);
        }
        
        StringBuilder response = new StringBuilder();
        response.append("📅 **Días seleccionados:** ");
        
        if (dias.isEmpty()) {
            response.append("Ninguno");
        } else {
            response.append(String.join(", ", dias));
        }
        
        response.append("\n\n");
        
        if (!dias.isEmpty()) {
            response.append("✅ Cuando termines de seleccionar, usa /confirmar_dias para continuar.\n");
            response.append("O sigue seleccionando más días.\n\n");
        }
        
        response.append("💡 Selecciona/deselecciona días usando los botones de arriba.");
        
        return response.toString();
    }
    
    private String confirmarDiasSeleccionados(String chatId, Arbitro arbitro) {
        Set<String> dias = diasSeleccionados.get(chatId);
        
        if (dias == null || dias.isEmpty()) {
            return "❌ No has seleccionado ningún día. Usa los botones de arriba para seleccionar días.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("✅ Días confirmados: ").append(String.join(", ", dias)).append("\n\n");
        response.append("🕐 Ahora selecciona las franjas horarias para cada día:\n\n");
        
        // Limpiar selección anterior y preparar para cada día
        diasSeleccionados.remove(chatId);
        
        // Configurar estado para cada día
        for (String dia : dias) {
            estadoModificacion.put(chatId + "_" + dia, dia);
            franjasSeleccionadas.put(chatId + "_" + dia, new HashSet<>());
            
            response.append("📅 **").append(dia).append("**:\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_08_10 → 08:00-10:00\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_10_12 → 10:00-12:00\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_12_14 → 12:00-14:00\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_14_16 → 14:00-16:00\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_16_18 → 16:00-18:00\n");
            response.append("• /franja_").append(dia.toLowerCase()).append("_18_20 → 18:00-20:00\n\n");
        }
        
        response.append("Cuando termines de seleccionar todas las franjas:\n");
        response.append("📝 /confirmar_disponibilidad_multiple → Guardar todos los cambios\n");
        response.append("❌ /cancelar_modificacion → Cancelar");
        
        return response.toString();
    }
    
    /**
     * Confirma los días seleccionados y envía la primera poll de franjas horarias
     */
    private String confirmarDiasYEnviarPrimeraFranja(String chatId, Arbitro arbitro) {
        Set<String> dias = diasSeleccionados.get(chatId);
        
        if (dias == null || dias.isEmpty()) {
            return """
                   ❌ No has seleccionado ningún día en la encuesta.
                   
                   Por favor:
                   1. Selecciona los días en la encuesta de arriba
                   2. Luego presiona este botón para confirmar
                   
                   Si no ves la encuesta, usa /modificar_disponibilidad para empezar de nuevo.
                   """;
        }
        
        // Obtener primer día para enviar poll de franjas
        String primerDia = dias.iterator().next();
        
        // Cambiar estado para seleccionar horarios de este día
        estadoModificacion.put(chatId, "SELECCIONANDO_HORARIOS_" + primerDia);
        
        // Preparar respuesta que incluirá envío de poll de franjas
        return String.format("""
               ✅ Días confirmados: %s
               
               🕐 Ahora selecciona las franjas horarias para %s.
               
               [ENVIAR_POLL_FRANJAS:%s]
               """, String.join(", ", dias), primerDia, primerDia);
    }
    
    /**
     * Confirma horarios para un día y continúa con el siguiente día o finaliza
     */
    private String confirmarHorariosYContinuar(String chatId, String dia, Arbitro arbitro) {
        // Verificar que tengamos días seleccionados
        Set<String> dias = diasSeleccionados.get(chatId);
        if (dias == null || dias.isEmpty()) {
            return "❌ Error: No hay días seleccionados. Empieza de nuevo con /modificar_disponibilidad.";
        }
        
        // Guardar las franjas para este día (ya están guardadas en franjasSeleccionadas)
        String key = chatId + "_" + dia;
        Set<String> franjas = franjasSeleccionadas.get(key);
        
        if (franjas == null || franjas.isEmpty()) {
            return "❌ No seleccionaste ninguna franja horaria para " + dia + ". Selecciona al menos una.";
        }
        
        // Encontrar el siguiente día
        java.util.List<String> diasOrdenados = new java.util.ArrayList<>(dias);
        java.util.Collections.sort(diasOrdenados);
        
        int indiceActual = diasOrdenados.indexOf(dia);
        
        if (indiceActual + 1 < diasOrdenados.size()) {
            // Hay más días, continuar con el siguiente
            String siguienteDia = diasOrdenados.get(indiceActual + 1);
            estadoModificacion.put(chatId, "SELECCIONANDO_HORARIOS_" + siguienteDia);
            
            return String.format("""
                   ✅ **Horarios para %s confirmados:** %s
                   
                   🕐 Ahora selecciona las franjas horarias para **%s**.
                   
                   [ENVIAR_POLL_FRANJAS:%s]
                   """, dia, String.join(", ", franjas), siguienteDia, siguienteDia);
        } else {
            // Es el último día, mostrar resumen final
            return mostrarResumenFinal(chatId, arbitro);
        }
    }
    
    /**
     * Muestra resumen final y botones de confirmación
     */
    private String mostrarResumenFinal(String chatId, Arbitro arbitro) {
        Set<String> dias = diasSeleccionados.get(chatId);
        
        StringBuilder resumen = new StringBuilder();
        resumen.append("📋 **RESUMEN DE DISPONIBILIDAD**\n\n");
        
        for (String dia : dias) {
            String key = chatId + "_" + dia;
            Set<String> franjas = franjasSeleccionadas.get(key);
            
            resumen.append("📅 **").append(dia).append(":**\n");
            if (franjas != null && !franjas.isEmpty()) {
                for (String franja : franjas) {
                    resumen.append("   ✅ ").append(franja).append("\n");
                }
            } else {
                resumen.append("   ❌ Sin disponibilidad\n");
            }
            resumen.append("\n");
        }
        
        resumen.append("¿Confirmas estos cambios en tu disponibilidad?\n\n");
        resumen.append("✅ Usa el botón **'Confirmar'** para guardar\n");
        resumen.append("❌ Usa el botón **'Cancelar'** para descartar");
        
        // Cambiar estado a esperar confirmación final
        estadoModificacion.put(chatId, "ESPERANDO_CONFIRMACION_FINAL");
        
        return resumen.toString() + "\n\n[MOSTRAR_BOTONES_CONFIRMACION]";
    }
    
    /**
     * Confirma y guarda la disponibilidad final
     */
    private String confirmarDisponibilidadFinal(String chatId, Arbitro arbitro) {
        try {
            Set<String> dias = diasSeleccionados.get(chatId);
            
            if (dias == null || dias.isEmpty()) {
                return "❌ No hay cambios para guardar.";
            }
            
            boolean exitoso = true;
            
            // Procesar cada día y sus franjas
            for (String dia : dias) {
                String key = chatId + "_" + dia;
                Set<String> franjas = franjasSeleccionadas.get(key);
                
                if (franjas != null && !franjas.isEmpty()) {
                    // Calcular horario continuo desde las franjas seleccionadas
                    String[] horario = calcularHorarioContinuo(franjas);
                    boolean resultado = disponibilidadService.modificarDisponibilidad(
                        arbitro, dia, horario[0], horario[1]
                    );
                    if (!resultado) {
                        exitoso = false;
                    }
                } else {
                    // Si no hay franjas seleccionadas, el árbitro no está disponible
                    boolean resultado = disponibilidadService.modificarDisponibilidad(
                        arbitro, dia, "No disponible", ""
                    );
                    if (!resultado) {
                        exitoso = false;
                    }
                }
            }
            
            // Guardar en Excel
            if (exitoso) {
                exitoso = disponibilidadService.guardarDisponibilidadEnExcel(arbitro);
            }
            
            // Limpiar estado
            limpiarEstadoModificacion(chatId);
            
            if (exitoso) {
                return """
                       ✅ **¡Disponibilidad actualizada exitosamente!**
                       
                       Tus cambios han sido guardados en el sistema y el archivo Excel.
                       
                       🔄 Usa /menu para volver al menú principal.
                       """;
            } else {
                return """
                       ⚠️ **Disponibilidad parcialmente actualizada**
                       
                       Algunos cambios no se pudieron guardar completamente.
                       
                       🔄 Usa /menu para volver al menú principal.
                       """;
            }
            
        } catch (Exception e) {
            logger.severe("Error confirmando disponibilidad final: " + e.getMessage());
            return "❌ Error guardando los cambios. Intenta de nuevo.";
        }
    }
    
    /**
     * Cancela completamente la modificación de disponibilidad
     */
    private String cancelarModificacionCompleta(String chatId) {
        limpiarEstadoModificacion(chatId);
        
        return """
               ❌ **Modificación cancelada**
               
               No se realizaron cambios en tu disponibilidad.
               
               🔄 Usa /menu para volver al menú principal.
               """;
    }
    
    /**
     * Limpia todo el estado de modificación para un usuario
     */
    private void limpiarEstadoModificacion(String chatId) {
        estadoModificacion.remove(chatId);
        diasSeleccionados.remove(chatId);
        
        // Limpiar franjas de todos los días
        java.util.Iterator<String> iterator = franjasSeleccionadas.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(chatId + "_")) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Convierte formato de franja de poll al formato del sistema
     */
    private String convertirFranjaPollaFormato(String franjaPoll) {
        switch (franjaPoll) {
            case "Mañana (8:00 - 12:00)":
                return "08:00-12:00";
            case "Tarde (14:00 - 18:00)":
                return "14:00-18:00";
            case "Noche (18:00 - 22:00)":
                return "18:00-22:00";
            default:
                return null;
        }
    }
    
    /**
     * Convierte franja de poll a array de horarios [inicio, fin]
     */
    private String[] convertirFranjaAHorarios(String franjaPoll) {
        switch (franjaPoll) {
            case "08:00 - 10:00":
                return new String[]{"08:00", "10:00"};
            case "10:00 - 12:00":
                return new String[]{"10:00", "12:00"};
            case "12:00 - 14:00":
                return new String[]{"12:00", "14:00"};
            case "14:00 - 16:00":
                return new String[]{"14:00", "16:00"};
            case "16:00 - 18:00":
                return new String[]{"16:00", "18:00"};
            case "18:00 - 20:00":
                return new String[]{"18:00", "20:00"};
            default:
                return null;
        }
    }
}
