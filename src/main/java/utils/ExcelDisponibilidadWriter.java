package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Arbitro;

public class ExcelDisponibilidadWriter {

    public static void generarArchivoDisponibilidades(List<Arbitro> arbitros, String rutaBase) {
        // Calcular el primer y último día de la semana actual
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate ultimoDiaSemana = hoy.with(DayOfWeek.SUNDAY);

        // Formatear el nombre del archivo
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

            // Crear encabezados
            crearEncabezados(sheet);

            // Agregar información de árbitros
            agregarArbitros(sheet, arbitros);

            // Ajustar columnas
            for (int i = 0; i < 22; i++) {
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

    private static void crearEncabezados(Sheet sheet) {
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

            // Horarios debajo del día
            String[] horarios = {"8:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00"};
            for (int i = 0; i < horarios.length; i++) {
                filaHorarios.createCell(colInicio + i).setCellValue(horarios[i]);
            }

            colInicio += 6; // Avanzar 6 columnas para el siguiente día
        }
    }

    private static void agregarArbitros(Sheet sheet, List<Arbitro> arbitros) {
        int filaInicio = 2; // Comenzar después de los encabezados

        for (Arbitro arbitro : arbitros) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(arbitro.getCedula());
            fila.createCell(1).setCellValue(arbitro.getNombre());
            fila.createCell(2).setCellValue(arbitro.getCategoria());

            // Inicializar todas las celdas de disponibilidad como "No"
            for (int col = 3; col < 27; col++) {
                fila.createCell(col).setCellValue("No");
            }
        }
    }
}
