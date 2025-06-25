package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Partido;

public class ExcelPartidoReader {
    public static List<Partido> leerPartidos(String rutaArchivo) {
        List<Partido> partidos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // Saltar cabecera
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String categoria = getCellString(row, 0);
                String municipio = getCellString(row, 1);
                String equipoLocal = getCellString(row, 2);
                String equipoVisitante = getCellString(row, 3);
                LocalDate fecha = LocalDate.parse(getCellString(row, 4));
                LocalTime hora = getCellHora(row, 5);
                String escenario = getCellString(row, 6);
                String colegioArbitros = getCellString(row, 7); // No usado aún
                String id = getCellString(row, 8);
                Partido partido = new Partido(categoria, municipio, equipoLocal, equipoVisitante, fecha, hora, escenario, id.isEmpty() ? java.util.UUID.randomUUID().toString() : id);
                partidos.add(partido);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo de partidos: " + e.getMessage());
        }
        return partidos;
    }

    private static String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            } else {
                return String.valueOf((int) cell.getNumericCellValue());
            }
        }
        return cell.toString().trim();
    }

    private static LocalTime getCellHora(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // Si es una celda de hora, convertir la fracción de día a LocalTime
                double excelDate = cell.getNumericCellValue();
                int totalSeconds = (int) Math.round((excelDate - Math.floor(excelDate)) * 24 * 60 * 60);
                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;
                return LocalTime.of(hours, minutes, seconds);
            }
        }
        // Si es texto, intentar parsear directamente
        return LocalTime.parse(cell.toString().trim());
    }
}
