package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Arbitro;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelArbitroReader {

    public static List<Arbitro> leerArbitros(String rutaArchivo) {
        List<Arbitro> arbitros = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook libro = new XSSFWorkbook(fis)) {

            Sheet hoja = libro.getSheetAt(0);
            boolean saltarEncabezado = true;

            for (Row fila : hoja) {
                if (saltarEncabezado) {
                    saltarEncabezado = false;
                    continue;
                }

                String nombre = getString(fila.getCell(0));
                String telefono = getString(fila.getCell(1));
                String cedula = getString(fila.getCell(2));
                String categoria = getString(fila.getCell(3));
                String activoStr = getString(fila.getCell(4)).toLowerCase().trim();
                boolean activo = activoStr.equals("sí") || activoStr.equals("si");

                Arbitro arbitro = new Arbitro(nombre, cedula, telefono, categoria, activo);
                arbitros.add(arbitro);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error al leer el archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }

        return arbitros;
    }

    // Función auxiliar para evitar errores con celdas vacías o con número en lugar de texto
    private static String getString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
