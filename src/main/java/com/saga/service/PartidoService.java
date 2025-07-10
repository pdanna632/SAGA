package com.saga.service;

import com.saga.model.Partido;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartidoService {

    public List<Partido> obtenerPartidosDesdeExcel() {
        List<Partido> partidos = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("Partidos.xlsx");
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet hoja = workbook.getSheetAt(0);

            // Saltar la fila de encabezados (índice 0)
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);

                if (fila == null) continue;

                String categoria = fila.getCell(0).getStringCellValue();
                String municipio = fila.getCell(1).getStringCellValue();
                String equipoLocal = fila.getCell(2).getStringCellValue();
                String equipoVisitante = fila.getCell(3).getStringCellValue();

                LocalDate fecha = fila.getCell(4).getLocalDateTimeCellValue().toLocalDate();
                LocalTime hora = fila.getCell(5).getLocalDateTimeCellValue().toLocalTime();

                String escenario = fila.getCell(6).getStringCellValue();

                Partido partido = new Partido(categoria, municipio, equipoLocal, equipoVisitante, fecha, hora, escenario);
                partidos.add(partido);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Podrías lanzar una excepción personalizada
        }

        return partidos;
    }
}
