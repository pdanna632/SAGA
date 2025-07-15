package com.saga.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.saga.model.Partido;

@Service
public class PartidoService {

    public List<Partido> obtenerPartidosDesdeExcel() {
        List<Partido> partidos = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data/Partidos.xlsx");
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                String categoria = fila.getCell(0).getStringCellValue();
                String municipio = fila.getCell(1).getStringCellValue();
                String equipoLocal = fila.getCell(2).getStringCellValue();
                String equipoVisitante = fila.getCell(3).getStringCellValue();

                Cell fechaCell = fila.getCell(4);
                LocalDate fecha;

                if (fechaCell.getCellType() == CellType.NUMERIC) {
                    fecha = fechaCell.getLocalDateTimeCellValue().toLocalDate();
                } else {
                    fecha = LocalDate.parse(fechaCell.getStringCellValue());
                }

                Cell horaCell = fila.getCell(5);
                LocalTime hora;

                if (horaCell.getCellType() == CellType.NUMERIC) {
                    hora = horaCell.getLocalDateTimeCellValue().toLocalTime();
                } else {
                    hora = LocalTime.parse(horaCell.getStringCellValue());
                }


                String id = fila.getCell(8).getStringCellValue(); // 🆔

                Partido partido = new Partido(
                    categoria, municipio, equipoLocal, equipoVisitante,
                    fecha, hora, id
                );

                partidos.add(partido);
            }

            System.out.println("✅ Excel leído correctamente. Total partidos: " + partidos.size());

        } catch (Exception e) {
            System.err.println("❌ Error leyendo el Excel:");
            e.printStackTrace();
        }

        return partidos;
    }

    public List<Partido> obtenerTodosLosPartidos() {
        return obtenerPartidosDesdeExcel();
    }

    public Partido buscarPartidoPorId(String id) {
    return obtenerPartidosDesdeExcel().stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
}

}
