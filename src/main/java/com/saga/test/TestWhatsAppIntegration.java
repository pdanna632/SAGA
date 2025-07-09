package com.saga.test;

import com.saga.model.Arbitro;
import com.saga.utils.ExcelArbitroWriter;

public class TestWhatsAppIntegration {
    
    public static void main(String[] args) {
        System.out.println("🧪 Probando integración WhatsApp SAGA...");
        
        // Prueba 1: Normalización de teléfonos
        System.out.println("\n📱 Prueba 1: Normalización de teléfonos");
        testNormalizacionTelefonos();
        
        // Prueba 2: Búsqueda por teléfono
        System.out.println("\n🔍 Prueba 2: Búsqueda por teléfono");
        testBusquedaPorTelefono();
        
        // Prueba 3: Crear árbitro de prueba
        System.out.println("\n👤 Prueba 3: Crear árbitro de prueba");
        testCrearArbitro();
        
        System.out.println("\n✅ Todas las pruebas completadas!");
    }
    
    private static void testNormalizacionTelefonos() {
        String[] telefonosTest = {
            "+573001234567",
            "573001234567", 
            "3001234567",
            "57 300 123 4567",
            "+57 300 123 4567",
            "(300) 123-4567",
            "300-123-4567"
        };
        
        for (String telefono : telefonosTest) {
            String limpio = ExcelArbitroWriter.limpiarTelefono(telefono);
            System.out.println("  " + telefono + " → " + limpio);
        }
    }
    
    private static void testBusquedaPorTelefono() {
        String rutaArchivo = "src/main/resources/data/Arbitros.xlsx";
        String telefono = "+573001234567";
        
        Arbitro arbitro = ExcelArbitroWriter.buscarPorTelefono(rutaArchivo, telefono);
        
        if (arbitro != null) {
            System.out.println("  ✅ Árbitro encontrado: " + arbitro.getNombre());
        } else {
            System.out.println("  ❌ Árbitro no encontrado para: " + telefono);
        }
    }
    
    private static void testCrearArbitro() {
        // Crear árbitro de prueba
        Arbitro arbitroTest = new Arbitro(
            "Juan Pérez Test",
            "12345678",
            "3001234567",
            "Nacional",
            true
        );
        
        System.out.println("  📝 Creando árbitro de prueba: " + arbitroTest.getNombre());
        
        // Nota: Descomenta la siguiente línea para realmente agregar el árbitro
        // String rutaArchivo = "src/main/resources/data/Arbitros.xlsx";
        // ExcelArbitroWriter.agregarArbitro(rutaArchivo, arbitroTest);
        
        System.out.println("  ⚠️  Árbitro de prueba NO agregado (línea comentada)");
    }
}
