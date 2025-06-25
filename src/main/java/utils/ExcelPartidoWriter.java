package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Partido;

public class ExcelPartidoWriter {
    public static void escribirPartidos(String rutaArchivo, List<Partido> partidos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Partidos");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("categoria_grupo");
            header.createCell(1).setCellValue("municipio");
            header.createCell(2).setCellValue("equipo_local");
            header.createCell(3).setCellValue("equipo_visitante");
            header.createCell(4).setCellValue("fecha");
            header.createCell(5).setCellValue("hora");
            header.createCell(6).setCellValue("escenario");
            header.createCell(7).setCellValue("colegio_arbitros");
            header.createCell(8).setCellValue("id");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (int i = 0; i < partidos.size(); i++) {
                Partido p = partidos.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(p.getCategoria());
                row.createCell(1).setCellValue(p.getMunicipio());
                row.createCell(2).setCellValue(p.getEquipoLocal());
                row.createCell(3).setCellValue(p.getEquipoVisitante());
                row.createCell(4).setCellValue(p.getFecha().format(dateFormatter));
                row.createCell(5).setCellValue(p.getHora().format(timeFormatter));
                row.createCell(6).setCellValue(p.getEscenario());
                row.createCell(7).setCellValue(""); // Puedes agregar el campo si lo tienes en Partido
                row.createCell(8).setCellValue(p.getId());
            }

            try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            System.out.println("Error escribiendo el archivo de partidos: " + e.getMessage());
        }
    }
}
