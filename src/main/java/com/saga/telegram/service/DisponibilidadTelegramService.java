package com.saga.telegram.service;

import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.model.Disponibilidad;
import com.saga.utils.ExcelArbitroReader;
import com.saga.utils.ExcelDisponibilidadReader;
import com.saga.utils.ExcelDisponibilidadWriter;

@Service
public class DisponibilidadTelegramService {
    
    private static final Logger logger = Logger.getLogger(DisponibilidadTelegramService.class.getName());
    
    /**
     * Busca un árbitro por su cédula
     */
    public Arbitro buscarArbitroPorCedula(String cedula) {
        try {
            String rutaArbitros = "src/main/resources/data/Arbitros.xlsx";
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(rutaArbitros);
            
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
     * Carga las disponibilidades de un árbitro desde Excel
     */
    public void cargarDisponibilidadesArbitro(Arbitro arbitro) {
        try {
            String rutaDisponibilidades = calcularRutaDisponibilidades();
            var mapaDisponibilidades = ExcelDisponibilidadReader.leerTodasLasDisponibilidades(rutaDisponibilidades);
            
            Disponibilidad disp = mapaDisponibilidades.get(arbitro.getCedula());
            if (disp != null) {
                arbitro.getDisponibilidades().clear();
                arbitro.agregarDisponibilidad(disp);
            }
        } catch (Exception e) {
            logger.severe("Error cargando disponibilidades del árbitro: " + e.getMessage());
        }
    }
    
    /**
     * Modifica la disponibilidad de un árbitro para un día específico
     */
    public boolean modificarDisponibilidad(Arbitro arbitro, String dia, String horaInicioStr, String horaFinStr) {
        try {
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);
            
            // Validar que la hora de fin sea posterior a la de inicio
            if (!horaFin.isAfter(horaInicio)) {
                logger.warning("Hora de fin debe ser posterior a hora de inicio");
                return false;
            }
            
            // Buscar o crear disponibilidad para este árbitro
            Disponibilidad disponibilidad = arbitro.getDisponibilidades().stream()
                    .filter(d -> d.getDisponibilidadSemanal().containsKey(dia))
                    .findFirst()
                    .orElse(null);
            
            if (disponibilidad == null) {
                disponibilidad = new Disponibilidad(arbitro.getCedula());
                arbitro.agregarDisponibilidad(disponibilidad);
            }
            
            // Eliminar disponibilidad anterior y agregar la nueva
            // Nota: Este es el mismo comportamiento que en la consola
            disponibilidad.eliminarDisponibilidad(dia, horaInicio, horaFin);
            disponibilidad.agregarDisponibilidad(dia, horaInicio, horaFin);
            
            logger.info("Disponibilidad modificada para árbitro " + arbitro.getNombre() + 
                       " el " + dia + " de " + horaInicio + " a " + horaFin);
            
            return true;
            
        } catch (Exception e) {
            logger.severe("Error modificando disponibilidad: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Guarda los cambios de disponibilidad en Excel
     */
    public boolean guardarDisponibilidadEnExcel(Arbitro arbitro) {
        try {
            String rutaArchivo = "src/main/resources/data/disponibilidades";
            ExcelDisponibilidadWriter.actualizarDisponibilidadArbitro(rutaArchivo, arbitro);
            
            logger.info("Disponibilidad guardada en Excel para árbitro: " + arbitro.getNombre());
            return true;
            
        } catch (Exception e) {
            logger.severe("Error guardando disponibilidad en Excel: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene la disponibilidad actual de un árbitro en formato texto
     */
    public String obtenerDisponibilidadTexto(Arbitro arbitro) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("📅 *Disponibilidad actual de ").append(arbitro.getNombre()).append("*\n\n");
            
            boolean tieneDisponibilidad = false;
            String[] dias = {"Jueves", "Viernes", "Sábado", "Domingo"};
            
            for (String dia : dias) {
                boolean tieneDia = false;
                for (Disponibilidad disp : arbitro.getDisponibilidades()) {
                    List<Disponibilidad.FranjaHoraria> franjas = disp.consultarDisponibilidad(dia);
                    if (!franjas.isEmpty()) {
                        if (!tieneDia) {
                            sb.append("🗓️ *").append(dia).append("*: ");
                            tieneDia = true;
                            tieneDisponibilidad = true;
                        }
                        for (Disponibilidad.FranjaHoraria franja : franjas) {
                            sb.append(franja.getInicio()).append(" - ").append(franja.getFin()).append(" ");
                        }
                        sb.append("\n");
                    }
                }
                if (!tieneDia) {
                    sb.append("🗓️ *").append(dia).append("*: No disponible\n");
                }
            }
            
            if (!tieneDisponibilidad) {
                sb.append("❌ No tiene disponibilidad registrada.");
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            logger.severe("Error obteniendo disponibilidad: " + e.getMessage());
            return "❌ Error obteniendo disponibilidad.";
        }
    }
    
    /**
     * Valida si un día es válido
     */
    public boolean validarDia(String dia) {
        return dia.equalsIgnoreCase("jueves") || 
               dia.equalsIgnoreCase("viernes") || 
               dia.equalsIgnoreCase("sábado") || 
               dia.equalsIgnoreCase("sabado") ||
               dia.equalsIgnoreCase("domingo");
    }
    
    /**
     * Normaliza el nombre del día
     */
    public String normalizarDia(String dia) {
        String diaLower = dia.toLowerCase().trim();
        switch (diaLower) {
            case "jueves":
                return "Jueves";
            case "viernes":
                return "Viernes";
            case "sábado":
            case "sabado":
                return "Sábado";
            case "domingo":
                return "Domingo";
            default:
                return null;
        }
    }
    
    /**
     * Valida formato de hora HH:mm
     */
    public boolean validarFormatoHora(String hora) {
        try {
            LocalTime.parse(hora);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Calcula la ruta de disponibilidades actual (mismo método que en Main)
     */
    private String calcularRutaDisponibilidades() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate primerDiaSemana = hoy.with(java.time.DayOfWeek.MONDAY);
        java.time.LocalDate ultimoDiaSemana = hoy.with(java.time.DayOfWeek.SUNDAY);
        String nombreArchivo = String.format("disponibilidades_%02d %02d a %02d %d.xlsx",
                primerDiaSemana.getMonthValue(),
                primerDiaSemana.getDayOfMonth(),
                ultimoDiaSemana.getDayOfMonth(),
                primerDiaSemana.getYear());
        return "src/main/resources/data/disponibilidades/" + nombreArchivo;
    }
}
