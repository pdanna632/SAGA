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
        
        return String.format("""
               %s
               
               � **Modificar Disponibilidad**
               
               Para cambiar tu disponibilidad, usa este formato:
               `dia:hora_inicio:hora_fin`
               
               **Ejemplos válidos:**
               • `jueves:09:00:13:00`
               • `viernes:14:00:18:00`
               • `sabado:08:00:12:00`
               • `domingo:15:00:19:00`
               
               **Instrucciones:**
               • Días: jueves, viernes, sabado, domingo
               • Horario: formato 24 horas (HH:mm)
               • La hora fin debe ser mayor que la hora inicio
               
               📝 Escribe tu nueva disponibilidad:
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
                return mostrarFormularioDisponibilidad("Jueves", arbitro);
            case "dia_viernes":
                return mostrarFormularioDisponibilidad("Viernes", arbitro);
            case "dia_sabado":
                return mostrarFormularioDisponibilidad("Sábado", arbitro);
            case "dia_domingo":
                return mostrarFormularioDisponibilidad("Domingo", arbitro);
                
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
}
