package main;

import java.util.List;
import java.util.Scanner;
import model.Arbitro;
import utils.ExcelArbitroReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== Bienvenido a SAGA - Sistema Automatizado de Gestión Arbitral =====");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Ingrese el usuario: ");
            String usuario = scanner.nextLine();
            System.out.print("Ingrese la contraseña: ");
            String contrasena = scanner.nextLine();

            if (!usuario.equals("ARBIANTIOQUIA") || !contrasena.equals("ADMIN")) {
                System.out.println("Credenciales incorrectas. Acceso denegado.");
                return;
            }

            int opcion;
            do {
                System.out.println("\n===== SAGA - Sistema Automatizado de Gestión Arbitral =====");
                System.out.println("1. Visualización de árbitros disponibles");
                System.out.println("2. Asignación de árbitros a partidos o eventos (por implementar)");
                System.out.println("3. Generación de informes semanales (por implementar)");
                System.out.println("4. Modificación extemporánea de asignaciones (por implementar)");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1 -> mostrarArbitrosDisponibles();
                    case 2 -> System.out.println("Funcionalidad de asignación de árbitros (por implementar)");
                    case 3 -> System.out.println("Funcionalidad de generación de informes semanales (por implementar)");
                    case 4 -> System.out.println("Funcionalidad de modificación extemporánea de asignaciones (por implementar)");
                    case 0 -> System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 0);
        }
    }

    private static void mostrarArbitrosDisponibles() {
        System.out.println("\n===== Árbitros disponibles =====");

        String ruta = "data/Arbitros.xlsx";
        List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(ruta);

        if (arbitros.isEmpty()) {
            System.out.println("No se encontraron árbitros.");
            return;
        }

        for (Arbitro a : arbitros) {
            if (a.isActivo()) {
                System.out.println(a);
            }
        }
    }
}
