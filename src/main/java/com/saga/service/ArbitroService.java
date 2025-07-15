package com.saga.service;

import com.saga.model.Arbitro;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArbitroService {

    public List<Arbitro> obtenerArbitrosDesdeExcel() {
        List<Arbitro> arbitros = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data/Arbitros.xlsx");
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                String nombre = fila.getCell(0).getStringCellValue();         // Nombre completo
                String telefono = fila.getCell(1).getStringCellValue();      // Número de teléfono
                String cedula = fila.getCell(2).getStringCellValue();        // Cédula
                String categoria = fila.getCell(3).getStringCellValue();     // Categoría
                String activoTexto = fila.getCell(4).getStringCellValue();   // ¿Activo?

                boolean activo = activoTexto.equalsIgnoreCase("sí") || activoTexto.equalsIgnoreCase("si");

                Arbitro arbitro = new Arbitro(nombre, cedula, telefono, categoria, activo);
                arbitros.add(arbitro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arbitros;
    }

    public Arbitro buscarArbitroPorNombre(String nombre) {
        System.out.println("🔍 Buscando árbitro: '" + nombre.trim() + "'");

        return obtenerArbitrosDesdeExcel().stream()
            .filter(a -> a.getNombre().trim().equalsIgnoreCase(nombre.trim()))
            .findFirst()
            .orElse(null);
    }
}
