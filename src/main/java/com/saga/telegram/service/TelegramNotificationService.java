package com.saga.telegram.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.model.Partido;
import com.saga.telegram.bot.SAGATelegramBot;
import com.saga.utils.ExcelArbitroReader;

@Service
public class TelegramNotificationService {
    
    private static final Logger logger = Logger.getLogger(TelegramNotificationService.class.getName());
    
    @Autowired
    private SAGATelegramBot telegramBot;
    
    @Autowired
    private TelegramAuthService authService;
    
    /**
     * Envía notificación a un árbitro cuando es asignado a un partido
     */
    public boolean notificarAsignacionPartido(String cedulaArbitro, Partido partido) {
        try {
            // Buscar el árbitro por cédula
            Arbitro arbitro = buscarArbitroPorCedula(cedulaArbitro);
            if (arbitro == null) {
                logger.warning("Árbitro no encontrado con cédula: " + cedulaArbitro);
                return false;
            }
            
            // Verificar si el árbitro tiene Chat ID vinculado
            String chatId = arbitro.getChatId();
            if (chatId == null || chatId.trim().isEmpty()) {
                logger.info("Árbitro " + arbitro.getNombre() + " no tiene Telegram vinculado");
                return false;
            }
            
            // Crear mensaje de notificación
            String mensaje = generarMensajeAsignacion(arbitro, partido);
            
            // Enviar notificación
            telegramBot.sendMessage(Long.parseLong(chatId), mensaje);
            
            logger.info("Notificación enviada a " + arbitro.getNombre() + " (Chat ID: " + chatId + ")");
            return true;
            
        } catch (Exception e) {
            logger.severe("Error enviando notificación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía notificación a múltiples árbitros
     */
    public void notificarAsignacionMultiple(List<String> cedulasArbitros, Partido partido) {
        for (String cedula : cedulasArbitros) {
            notificarAsignacionPartido(cedula, partido);
        }
    }
    
    /**
     * Busca un árbitro por cédula en el archivo Excel
     */
    private Arbitro buscarArbitroPorCedula(String cedula) {
        try {
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros("src/main/resources/data/Arbitros.xlsx");
            
            return arbitros.stream()
                    .filter(a -> a.getCedula().equals(cedula))
                    .findFirst()
                    .orElse(null);
                    
        } catch (Exception e) {
            logger.severe("Error buscando árbitro por cédula: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Genera el mensaje de notificación de asignación
     */
    private String generarMensajeAsignacion(Arbitro arbitro, Partido partido) {
        return String.format("""
               ⚽ *¡Nueva Asignación de Partido!*
               
               Hola *%s*, has sido asignado a un nuevo partido:
               
               📅 *Fecha:* %s
               ⏰ *Hora:* %s
               🏟️ *Categoría:* %s
               📍 *Municipio:* %s
               
               🆚 *Equipos:*
               🏠 Local: %s
               🚌 Visitante: %s
               
               📋 *Detalles:*
               🆔 ID Partido: %s
               🏟️ Escenario: %s
               
               💡 *Recuerda confirmar tu disponibilidad en el sistema SAGA*
               
               ¿Necesitas más información? Escribe /info para ver tus datos.
               """,
               arbitro.getNombre().split(" ")[0],
               partido.getFecha(),
               partido.getHora(),
               partido.getCategoria(),
               partido.getMunicipio(),
               partido.getEquipoLocal(),
               partido.getEquipoVisitante(),
               partido.getId(),
               partido.getEscenario());
    }
    
    /**
     * Envía recordatorio de partido próximo
     */
    public boolean enviarRecordatorioPartido(String cedulaArbitro, Partido partido, int horasAntes) {
        try {
            Arbitro arbitro = buscarArbitroPorCedula(cedulaArbitro);
            if (arbitro == null || arbitro.getChatId() == null) {
                return false;
            }
            
            String mensaje = String.format("""
                   ⏰ *Recordatorio de Partido*
                   
                   Hola *%s*, tu partido es en *%d horas*:
                   
                   📅 %s a las %s
                   🆚 %s vs %s
                   📍 %s
                   
                   ¡No olvides llegar con tiempo!
                   """,
                   arbitro.getNombre().split(" ")[0],
                   horasAntes,
                   partido.getFecha(),
                   partido.getHora(),
                   partido.getEquipoLocal(),
                   partido.getEquipoVisitante(),
                   partido.getMunicipio());
            
            telegramBot.sendMessage(Long.parseLong(arbitro.getChatId()), mensaje);
            return true;
            
        } catch (Exception e) {
            logger.severe("Error enviando recordatorio: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica qué árbitros tienen Telegram vinculado
     */
    public void verificarArbitrosConTelegram() {
        try {
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros("src/main/resources/data/Arbitros.xlsx");
            
            long conTelegram = arbitros.stream()
                    .filter(a -> a.getChatId() != null && !a.getChatId().trim().isEmpty())
                    .count();
            
            logger.info("Árbitros totales: " + arbitros.size());
            logger.info("Árbitros con Telegram vinculado: " + conTelegram);
            logger.info("Árbitros sin Telegram: " + (arbitros.size() - conTelegram));
            
        } catch (Exception e) {
            logger.severe("Error verificando árbitros: " + e.getMessage());
        }
    }
}
