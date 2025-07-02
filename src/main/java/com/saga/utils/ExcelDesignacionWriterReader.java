package com.saga.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.saga.model.Arbitro;
import com.saga.model.Designacion;
import com.saga.model.Partido;

public class ExcelDesignacionWriterReader {
    public static String calcularRutaDesignaciones() {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate ultimoDiaSemana = hoy.with(DayOfWeek.SUNDAY);
        String nombreArchivo = String.format("designaciones_%02d %02d a %02d %d.xlsx",
                primerDiaSemana.getMonthValue(),
                primerDiaSemana.getDayOfMonth(),
                ultimoDiaSemana.getDayOfMonth(),
                primerDiaSemana.getYear());
        return "src/main/resources/data/designaciones/" + nombreArchivo;
    }

    public static void guardarDesignaciones(List<Designacion> designaciones) {
        String ruta = calcularRutaDesignaciones();
        File archivo = new File(ruta);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Designaciones");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("partido_id");
            header.createCell(1).setCellValue("arbitro_cc");
            header.createCell(2).setCellValue("rol");
            header.createCell(3).setCellValue("realizado");
            for (int i = 0; i < designaciones.size(); i++) {
                Designacion d = designaciones.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(d.getPartido().getId());
                row.createCell(1).setCellValue(d.getArbitro().getCedula());
                row.createCell(2).setCellValue(d.getRol());
                row.createCell(3).setCellValue(d.isRealizado());
            }
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            System.err.println("Error guardando designaciones: " + e.getMessage());
        }
    }

    public static List<Designacion> leerDesignaciones(List<Partido> partidos, List<Arbitro> arbitros) {
        String ruta = calcularRutaDesignaciones();
        File archivo = new File(ruta);
        List<Designacion> designaciones = new ArrayList<>();
        if (!archivo.exists()) return designaciones;
        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // Saltar cabecera
            Map<String, Partido> partidoMap = partidos.stream().collect(Collectors.toMap(Partido::getId, p -> p));
            Map<String, Arbitro> arbitroMap = arbitros.stream().collect(Collectors.toMap(Arbitro::getCedula, a -> a));
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String partidoId = row.getCell(0).getStringCellValue();
                String arbitroCC = row.getCell(1).getStringCellValue();
                String rol = row.getCell(2).getStringCellValue();
                boolean realizado = false;
                if (row.getLastCellNum() > 3 && row.getCell(3) != null) {
                    realizado = row.getCell(3).getBooleanCellValue();
                }
                Partido partido = partidoMap.get(partidoId);
                Arbitro arbitro = arbitroMap.get(arbitroCC);
                if (partido != null && arbitro != null) {
                    designaciones.add(new Designacion(arbitro, partido, rol, realizado));
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo designaciones: " + e.getMessage());
        }
        return designaciones;
    }
}
