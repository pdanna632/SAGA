package com.saga.test;

import java.util.List;

import com.saga.model.Arbitro;
import com.saga.telegram.service.DisponibilidadTelegramService;
import com.saga.utils.ExcelArbitroReader;

/**
 * Clase de prueba para verificar la funcionalidad de disponibilidades en Telegram
 */
public class TestDisponibilidadTelegram {
    
    public static void main(String[] args) {
        System.out.println("🧪 ===== PRUEBA DE DISPONIBILIDADES TELEGRAM =====\n");
        
        DisponibilidadTelegramService service = new DisponibilidadTelegramService();
        
        // Probar búsqueda de árbitro
        System.out.println("1️⃣ Probando búsqueda de árbitro por cédula...");
        Arbitro arbitro = service.buscarArbitroPorCedula("1023456789");
        
        if (arbitro != null) {
            System.out.println("✅ Árbitro encontrado: " + arbitro.getNombre());
            
            // Cargar disponibilidades
            System.out.println("\n2️⃣ Cargando disponibilidades actuales...");
            service.cargarDisponibilidadesArbitro(arbitro);
            
            // Mostrar disponibilidades actuales
            System.out.println("\n3️⃣ Disponibilidades actuales:");
            String disponibilidadTexto = service.obtenerDisponibilidadTexto(arbitro);
            System.out.println(disponibilidadTexto);
            
            // Probar modificación
            System.out.println("\n4️⃣ Probando modificación de disponibilidad...");
            boolean exito = service.modificarDisponibilidad(arbitro, "Viernes", "10:00", "14:00");
            System.out.println("Modificación exitosa: " + exito);
            
            if (exito) {
                // Mostrar disponibilidades modificadas
                System.out.println("\n5️⃣ Disponibilidades después de la modificación:");
                String nuevaDisponibilidad = service.obtenerDisponibilidadTexto(arbitro);
                System.out.println(nuevaDisponibilidad);
                
                // Probar guardado en Excel
                System.out.println("\n6️⃣ Guardando en Excel...");
                boolean guardado = service.guardarDisponibilidadEnExcel(arbitro);
                System.out.println("Guardado en Excel exitoso: " + guardado);
            }
            
        } else {
            System.out.println("❌ No se encontró árbitro con cédula 1023456789");
            
            // Listar árbitros disponibles
            System.out.println("\n📋 Árbitros disponibles en el sistema:");
            try {
                List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros("src/main/resources/data/Arbitros.xlsx");
                for (Arbitro a : arbitros) {
                    System.out.println("  • " + a.getCedula() + " - " + a.getNombre());
                }
            } catch (Exception e) {
                System.out.println("❌ Error leyendo árbitros: " + e.getMessage());
            }
        }
        
        // Probar validaciones
        System.out.println("\n7️⃣ Probando validaciones...");
        System.out.println("Día 'viernes' válido: " + service.validarDia("viernes"));
        System.out.println("Día 'lunes' válido: " + service.validarDia("lunes"));
        System.out.println("Hora '14:30' válida: " + service.validarFormatoHora("14:30"));
        System.out.println("Hora '25:00' válida: " + service.validarFormatoHora("25:00"));
        System.out.println("Normalizar 'sabado': " + service.normalizarDia("sabado"));
        
        System.out.println("\n✅ ===== PRUEBA COMPLETADA =====");
    }
}
