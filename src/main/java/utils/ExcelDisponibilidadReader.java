package utils;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Disponibilidad;

public class ExcelDisponibilidadReader {

    public static Disponibilidad leerDisponibilidades(String rutaArchivo) {
        Disponibilidad disponibilidad = null;

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook libro = new XSSFWorkbook(fis)) {

            Sheet hoja = libro.getSheetAt(0);

            // Leer la cédula del árbitro desde la primera fila
            Row fila = hoja.getRow(2); // Suponiendo que la cédula está en la fila 2, columna 0
            if (fila != null) {
                Cell celdaCedula = fila.getCell(0);
                if (celdaCedula != null) {
                    String cedulaArbitro = celdaCedula.getStringCellValue();
                    disponibilidad = new Disponibilidad(cedulaArbitro); // Inicializar con la cédula
                }
            }

            // Leer las disponibilidades (implementación adicional aquí)

        } catch (Exception e) {
            System.err.println("⚠️ Error al leer el archivo Excel de disponibilidades: " + e.getMessage());
            e.printStackTrace();
        }

        return disponibilidad;
    }
}
