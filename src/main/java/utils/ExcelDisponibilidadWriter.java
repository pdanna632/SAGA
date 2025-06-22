package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Arbitro;

public class ExcelDisponibilidadWriter {

    public static void generarArchivoDisponibilidades(List<Arbitro> arbitros, String rutaBase) {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate ultimoDiaSemana = hoy.with(DayOfWeek.SUNDAY);

        String nombreArchivo = String.format("disponibilidades_%02d %02d a %02d %d.xlsx",
                primerDiaSemana.getMonthValue(),
                primerDiaSemana.getDayOfMonth(),
                ultimoDiaSemana.getDayOfMonth(),
                primerDiaSemana.getYear());

        String rutaCompleta = rutaBase + File.separator + nombreArchivo;

        File archivo = new File(rutaCompleta);
        if (archivo.exists()) {
            System.out.println("El archivo de disponibilidades ya existe: " + rutaCompleta);
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Disponibilidades");

            // Crear estilos
            CellStyle estiloTituloDia = crearEstiloTituloDia(workbook);
            CellStyle estiloNoDisponible = crearEstiloNoDisponible(workbook);

            // Crear encabezados
            crearEncabezados(sheet, estiloTituloDia);

            // Agregar información de árbitros
            agregarArbitros(sheet, arbitros, estiloNoDisponible);

            // Ajustar columnas
            for (int i = 0; i < 31; i++) { // Ajustar para incluir todas las columnas (con espacios)
                sheet.autoSizeColumn(i);
            }

            // Guardar el archivo
            try (FileOutputStream fos = new FileOutputStream(rutaCompleta)) {
                workbook.write(fos);
            }

            System.out.println("Archivo de disponibilidades creado: " + rutaCompleta);
        } catch (Exception e) {
            System.err.println("⚠️ Error al generar el archivo de disponibilidades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static CellStyle crearEstiloTituloDia(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // Tamaño de fuente más grande
        estilo.setFont(font);
        estilo.setAlignment(HorizontalAlignment.CENTER); // Centrar texto
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        return estilo;
    }

    private static CellStyle crearEstiloNoDisponible(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        estilo.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // Color rosado claro
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER); // Centrar texto
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setBorderBottom(BorderStyle.THIN); // Borde inferior
        estilo.setBorderTop(BorderStyle.THIN); // Borde superior
        estilo.setBorderLeft(BorderStyle.THIN); // Borde izquierdo
        estilo.setBorderRight(BorderStyle.THIN); // Borde derecho
        return estilo;
    }

    private static void crearEncabezados(Sheet sheet, CellStyle estiloTituloDia) {
        Row filaDias = sheet.createRow(0);
        Row filaHorarios = sheet.createRow(1);

        // Encabezados iniciales
        filaDias.createCell(0).setCellValue("Cédula");
        filaDias.createCell(1).setCellValue("Nombre");
        filaDias.createCell(2).setCellValue("Categoría");

        // Días de la semana
        String[] dias = {"Jueves", "Viernes", "Sábado", "Domingo"};
        int colInicio = 3;

        for (String dia : dias) {
            // Combinar celdas para el día
            sheet.addMergedRegion(new CellRangeAddress(0, 0, colInicio, colInicio + 5));
            Cell celdaDia = filaDias.createCell(colInicio);
            celdaDia.setCellValue(dia);
            celdaDia.setCellStyle(estiloTituloDia);

            // Horarios debajo del día
            String[] horarios = {"8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
            for (int i = 0; i < horarios.length; i++) {
                Cell celdaHorario = filaHorarios.createCell(colInicio + i);
                celdaHorario.setCellValue(horarios[i]);
            }

            // Agregar una columna de espacio entre días
            colInicio += 7; // Avanzar 6 columnas para el día + 1 columna de espacio
        }
    }

    private static void agregarArbitros(Sheet sheet, List<Arbitro> arbitros, CellStyle estiloNoDisponible) {
        int filaInicio = 2; // Comenzar después de los encabezados

        for (Arbitro arbitro : arbitros) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(arbitro.getCedula());
            fila.createCell(1).setCellValue(arbitro.getNombre());
            fila.createCell(2).setCellValue(arbitro.getCategoria());

            // Inicializar todas las celdas de disponibilidad como "No"
            for (int col = 3; col < 31; col++) { // Ajustar para incluir columnas adicionales (con espacios)
                Cell celda = fila.createCell(col);
                celda.setCellValue("No");
                celda.setCellStyle(estiloNoDisponible);
            }
        }
    }
}
