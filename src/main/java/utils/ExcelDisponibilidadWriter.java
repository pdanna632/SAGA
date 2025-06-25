package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
import model.Disponibilidad;

public class ExcelDisponibilidadWriter {

    public static void generarArchivoDisponibilidades(List<Arbitro> arbitros, String rutaBase) {
        // Filtrar solo árbitros activos
        List<Arbitro> arbitrosActivos = arbitros.stream().filter(Arbitro::isActivo).toList();
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

            // Agregar información de árbitros activos
            agregarArbitros(sheet, arbitrosActivos, estiloNoDisponible);

            // Ajustar columnas
            for (int i = 0; i < 27; i++) { // Ajustar para incluir todas las columnas
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

            colInicio += 6; // Avanzar 6 columnas para el siguiente día
        }
    }

    private static void agregarArbitros(Sheet sheet, List<Arbitro> arbitros, CellStyle estiloNoDisponible) {
        int filaInicio = 2; // Comenzar después de los encabezados

        for (Arbitro arbitro : arbitros) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(arbitro.getCedula());
            fila.createCell(1).setCellValue(arbitro.getNombre());
            fila.createCell(2).setCellValue(arbitro.getCategoria());

            // Inicializar todas las celdas de disponibilidad como coloreadas
            for (int col = 3; col < 27; col++) { // Ajustar para incluir todas las columnas
                Cell celda = fila.createCell(col);
                celda.setCellStyle(estiloNoDisponible); // Aplicar color sin texto
            }
        }
    }

    public static void actualizarDisponibilidades(String rutaArchivo, List<Arbitro> arbitros) {
        // Filtrar solo árbitros activos
        List<Arbitro> arbitrosActivos = arbitros.stream().filter(Arbitro::isActivo).toList();
        File archivo = new File(rutaArchivo);

        try (Workbook workbook = archivo.exists() ? new XSSFWorkbook(new FileInputStream(archivo)) : new XSSFWorkbook()) {
            Sheet sheet = workbook.getSheet("Disponibilidades");
            if (sheet == null) {
                sheet = workbook.createSheet("Disponibilidades");
                crearEncabezados(sheet, workbook);
            }

            // Iterar sobre todos los árbitros activos
            for (Arbitro arbitro : arbitrosActivos) {
                String cedulaArbitro = arbitro.getCedula();
                List<Disponibilidad> disponibilidades = arbitro.getDisponibilidades(); // Obtener la lista de disponibilidades del árbitro

                boolean filaEncontrada = false; // Bandera para detener la iteración una vez que se encuentra la fila correcta
                for (int filaIndex = 2; filaIndex <= sheet.getLastRowNum(); filaIndex++) {
                    Row fila = sheet.getRow(filaIndex);
                    if (fila == null) continue;

                    // Verificar si la fila corresponde al árbitro actual
                    String cedulaExcel = fila.getCell(0).getStringCellValue();
                    if (cedulaArbitro == null || !cedulaArbitro.equals(cedulaExcel)) {
                        continue;
                    }

                    // Escribir las disponibilidades en la fila correspondiente
                    for (Disponibilidad disponibilidad : disponibilidades) {
                        for (Map.Entry<String, List<Disponibilidad.FranjaHoraria>> entry : disponibilidad.getDisponibilidadSemanal().entrySet()) {
                            String dia = entry.getKey();
                            List<Disponibilidad.FranjaHoraria> franjas = entry.getValue();

                            int colInicio = 3 + (dia.equals("Jueves") ? 0 : dia.equals("Viernes") ? 6 : dia.equals("Sábado") ? 12 : 18);

                            for (Disponibilidad.FranjaHoraria franja : franjas) {
                                LocalTime inicio = franja.getInicio();
                                LocalTime fin = franja.getFin();

                                // Iterar sobre las franjas de 2 horas dentro del rango de tiempo
                                while (!inicio.equals(fin)) {
                                    int col = colInicio + ((inicio.getHour() - 8) / 2); // Calcular la columna correspondiente
                                    Cell celda = fila.getCell(col);
                                    if (celda == null) {
                                        celda = fila.createCell(col);
                                    }

                                    // Configurar estilo de celda para disponibilidad (color verde)
                                    CellStyle estilo = workbook.createCellStyle();
                                    estilo.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                                    estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                    estilo.setBorderBottom(BorderStyle.THIN);
                                    estilo.setBorderTop(BorderStyle.THIN);
                                    estilo.setBorderLeft(BorderStyle.THIN);
                                    estilo.setBorderRight(BorderStyle.THIN);
                                    celda.setCellStyle(estilo);

                                    // Avanzar 2 horas
                                    inicio = inicio.plusHours(2);
                                }
                            }
                        }
                    }

                    filaEncontrada = true; // Marcar que la fila correcta fue encontrada
                    break; // Detener la iteración
                }

                if (!filaEncontrada) {
                    System.err.println("⚠️ No se encontró la fila correspondiente al árbitro con cédula: " + cedulaArbitro);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                workbook.write(fos);
            }

            System.out.println("Disponibilidades actualizadas correctamente en el archivo Excel.");
        } catch (Exception e) {
            System.err.println("⚠️ Error al actualizar el archivo Excel de disponibilidades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void actualizarDisponibilidadArbitro(String rutaBase, Arbitro arbitro) {
        // Calcular la ruta completa del archivo Excel
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate ultimoDiaSemana = hoy.with(DayOfWeek.SUNDAY);
        String nombreArchivo = String.format("disponibilidades_%02d %02d a %02d %d.xlsx",
                primerDiaSemana.getMonthValue(),
                primerDiaSemana.getDayOfMonth(),
                ultimoDiaSemana.getDayOfMonth(),
                primerDiaSemana.getYear());
        String rutaArchivo = rutaBase + File.separator + nombreArchivo;

        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            System.err.println("⚠️ El archivo no existe: " + rutaArchivo);
            return;
        }

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(archivo))) {
            Sheet sheet = workbook.getSheet("Disponibilidades");
            if (sheet == null) {
                System.err.println("⚠️ La hoja 'Disponibilidades' no existe en el archivo: " + rutaArchivo);
                return;
            }

            String cedulaArbitro = arbitro.getCedula();
            List<Disponibilidad> disponibilidades = arbitro.getDisponibilidades(); // Obtener la lista de disponibilidades del árbitro

            boolean filaEncontrada = false; // Bandera para detener la iteración una vez que se encuentra la fila correcta
            for (int filaIndex = 2; filaIndex <= sheet.getLastRowNum(); filaIndex++) {
                Row fila = sheet.getRow(filaIndex);
                if (fila == null) continue;

                // Verificar si la fila corresponde al árbitro actual
                String cedulaExcel = fila.getCell(0).getStringCellValue();
                if (cedulaArbitro == null || !cedulaArbitro.equals(cedulaExcel)) {
                    continue;
                }

                // Escribir las disponibilidades en la fila correspondiente
                for (Disponibilidad disponibilidad : disponibilidades) {
                    for (Map.Entry<String, List<Disponibilidad.FranjaHoraria>> entry : disponibilidad.getDisponibilidadSemanal().entrySet()) {
                        String dia = entry.getKey();
                        List<Disponibilidad.FranjaHoraria> franjas = entry.getValue();

                        int colInicio = 3 + (dia.equals("Jueves") ? 0 : dia.equals("Viernes") ? 6 : dia.equals("Sábado") ? 12 : 18);

                        for (Disponibilidad.FranjaHoraria franja : franjas) {
                            LocalTime inicio = franja.getInicio();
                            LocalTime fin = franja.getFin();

                            // Iterar sobre las franjas de 2 horas dentro del rango de tiempo
                            while (!inicio.equals(fin)) {
                                int col = colInicio + ((inicio.getHour() - 8) / 2); // Calcular la columna correspondiente
                                Cell celda = fila.getCell(col);
                                if (celda == null) {
                                    celda = fila.createCell(col);
                                }

                                // Configurar estilo de celda para disponibilidad (color verde)
                                CellStyle estilo = workbook.createCellStyle();
                                estilo.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                                estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                estilo.setBorderBottom(BorderStyle.THIN);
                                estilo.setBorderTop(BorderStyle.THIN);
                                estilo.setBorderLeft(BorderStyle.THIN);
                                estilo.setBorderRight(BorderStyle.THIN);
                                celda.setCellStyle(estilo);

                                // Avanzar 2 horas
                                inicio = inicio.plusHours(2);
                            }
                        }
                    }
                }

                filaEncontrada = true; // Marcar que la fila correcta fue encontrada
                break; // Detener la iteración
            }

            if (!filaEncontrada) {
                System.err.println("⚠️ No se encontró la fila correspondiente al árbitro con cédula: " + cedulaArbitro);
            }

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                workbook.write(fos);
            }

            System.out.println("Disponibilidad actualizada correctamente para el árbitro con cédula: " + cedulaArbitro);
        } catch (Exception e) {
            System.err.println("⚠️ Error al actualizar la disponibilidad del árbitro en el archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void crearEncabezados(Sheet sheet, Workbook workbook) {
        Row filaDias = sheet.createRow(0);
        Row filaHorarios = sheet.createRow(1);

        String[] dias = {"Jueves", "Viernes", "Sábado", "Domingo"};
        int colInicio = 3;

        for (String dia : dias) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, colInicio, colInicio + 5));
            Cell celdaDia = filaDias.createCell(colInicio);
            celdaDia.setCellValue(dia);

            String[] horarios = {"8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
            for (int i = 0; i < horarios.length; i++) {
                filaHorarios.createCell(colInicio + i).setCellValue(horarios[i]);
            }

            colInicio += 6;
        }
    }
}
