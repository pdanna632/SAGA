package com.saga.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.saga.model.Arbitro;

public class ExcelArbitroWriter {
    
    /**
     * Agrega un nuevo árbitro al archivo Excel
     */
    public static void agregarArbitro(String rutaArchivo, Arbitro arbitro) {
        try {
            // Leer el archivo existente
            List<Arbitro> arbitrosExistentes = ExcelArbitroReader.leerArbitros(rutaArchivo);
            
            // Agregar el nuevo árbitro
            arbitrosExistentes.add(arbitro);
            
            // Reescribir todo el archivo
            escribirArbitros(rutaArchivo, arbitrosExistentes);
            
            System.out.println("✅ Árbitro agregado exitosamente: " + arbitro.getNombre());
            
        } catch (Exception e) {
            System.err.println("❌ Error al agregar árbitro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza el teléfono de un árbitro existente por su cédula
     */
    public static boolean actualizarTelefono(String rutaArchivo, String cedula, String nuevoTelefono) {
        try {
            // Leer el archivo existente
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(rutaArchivo);
            
            // Buscar y actualizar el árbitro
            boolean encontrado = false;
            for (Arbitro arbitro : arbitros) {
                if (arbitro.getCedula().equals(cedula)) {
                    arbitro.setTelefono(nuevoTelefono);
                    encontrado = true;
                    System.out.println("✅ Teléfono actualizado para: " + arbitro.getNombre());
                    break;
                }
            }
            
            if (encontrado) {
                // Reescribir todo el archivo
                escribirArbitros(rutaArchivo, arbitros);
                return true;
            } else {
                System.out.println("❌ No se encontró árbitro con cédula: " + cedula);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar teléfono: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca un árbitro por cédula
     */
    public static Arbitro buscarPorCedula(String rutaArchivo, String cedula) {
        try {
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(rutaArchivo);
            
            for (Arbitro arbitro : arbitros) {
                if (arbitro.getCedula().equals(cedula)) {
                    return arbitro;
                }
            }
            
            return null; // No encontrado
            
        } catch (Exception e) {
            System.err.println("❌ Error al buscar árbitro: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Busca un árbitro por teléfono (limpiando formato)
     */
    public static Arbitro buscarPorTelefono(String rutaArchivo, String telefono) {
        try {
            String telefonoLimpio = limpiarTelefono(telefono);
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(rutaArchivo);
            
            for (Arbitro arbitro : arbitros) {
                String telefonoArbitro = limpiarTelefono(arbitro.getTelefono());
                if (telefonoArbitro.equals(telefonoLimpio)) {
                    return arbitro;
                }
            }
            
            return null; // No encontrado
            
        } catch (Exception e) {
            System.err.println("❌ Error al buscar árbitro por teléfono: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Limpia el formato del teléfono (quita +57, 57, espacios, etc.)
     */
    public static String limpiarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "";
        }
        
        // Quitar espacios, guiones, paréntesis
        String limpio = telefono.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Quitar +57 o 57 del inicio
        if (limpio.startsWith("+57")) {
            limpio = limpio.substring(3);
        } else if (limpio.startsWith("57") && limpio.length() > 10) {
            limpio = limpio.substring(2);
        }
        
        return limpio;
    }
    
    /**
     * Reescribe completamente el archivo Excel con la lista de árbitros
     */
    private static void escribirArbitros(String rutaArchivo, List<Arbitro> arbitros) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Arbitros");
            
            // Crear encabezado
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nombre");
            header.createCell(1).setCellValue("Telefono");
            header.createCell(2).setCellValue("Cedula");
            header.createCell(3).setCellValue("Categoria");
            header.createCell(4).setCellValue("Activo");
            
            // Escribir datos
            for (int i = 0; i < arbitros.size(); i++) {
                Arbitro arbitro = arbitros.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(arbitro.getNombre());
                row.createCell(1).setCellValue(arbitro.getTelefono());
                row.createCell(2).setCellValue(arbitro.getCedula());
                row.createCell(3).setCellValue(arbitro.getCategoria());
                row.createCell(4).setCellValue(arbitro.isActivo() ? "Sí" : "No");
            }
            
            // Guardar archivo
            try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
                workbook.write(fos);
            }
        }
    }
}
