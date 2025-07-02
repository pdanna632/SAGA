package com.saga.utils;

import java.io.FileInputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.saga.model.Disponibilidad;

public class ExcelDisponibilidadReader {

    /**
     * Lee todas las disponibilidades del archivo Excel y retorna un Map de cédula a Disponibilidad.
     */
    public static Map<String, Disponibilidad> leerTodasLasDisponibilidades(String rutaArchivo) {
        Map<String, Disponibilidad> mapaDisponibilidades = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook libro = new XSSFWorkbook(fis)) {

            Sheet hoja = libro.getSheetAt(0);
            if (hoja == null) return mapaDisponibilidades;

            // Días y horarios según el formato del archivo
            String[] dias = {"Jueves", "Viernes", "Sábado", "Domingo"};
            String[] horarios = {"8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
            int colInicio = 3;

            // Recorrer filas de árbitros (desde la fila 2 en adelante)
            for (int filaIdx = 2; filaIdx <= hoja.getLastRowNum(); filaIdx++) {
                Row fila = hoja.getRow(filaIdx);
                if (fila == null) continue;
                Cell celdaCedula = fila.getCell(0);
                if (celdaCedula == null) continue;
                String cedula = celdaCedula.getStringCellValue();
                Disponibilidad disp = new Disponibilidad(cedula);

                int col = colInicio;
                for (String dia : dias) {
                    for (String hora : horarios) {
                        Cell celda = fila.getCell(col);
                        boolean disponible = false;
                        if (celda != null) {
                            CellStyle style = celda.getCellStyle();
                            if (style != null && style.getFillForegroundColor() == IndexedColors.LIGHT_GREEN.getIndex()) {
                                disponible = true;
                            }
                        }
                        // Solo agregar como disponible si la celda es verde
                        if (disponible) {
                            LocalTime inicio = LocalTime.parse(hora.length() == 4 ? "0" + hora : hora); // Asegura formato HH:mm
                            LocalTime fin = inicio.plusHours(2); // Cada franja es de 2 horas
                            disp.agregarDisponibilidad(dia, inicio, fin);
                        }
                        col++;
                    }
                }
                mapaDisponibilidades.put(cedula, disp);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al leer el archivo Excel de disponibilidades: " + e.getMessage());
            e.printStackTrace();
        }
        return mapaDisponibilidades;
    }
}
