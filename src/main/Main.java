// filepath: d:uni\Ing Software\Projecto\proyecto 1\SAGA\src\main\Main.java
package main;

import java.util.Scanner;

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
                System.out.println("1. Verificación de identidad del usuario (por implementar)");
                System.out.println("2. Visualización de árbitros disponibles (por implementar)");
                System.out.println("3. Asignación de árbitros a partidos o eventos (por implementar)");
                System.out.println("4. Generación de informes semanales (por implementar)");
                System.out.println("5. Modificación extemporánea de asignaciones (por implementar)");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                switch (opcion) {
                    case 1 -> System.out.println("Funcionalidad de verificación de identidad (por implementar)");
                    case 2 -> System.out.println("Funcionalidad de visualización de árbitros disponibles (por implementar)");
                    case 3 -> System.out.println("Funcionalidad de asignación de árbitros (por implementar)");
                    case 4 -> System.out.println("Funcionalidad de generación de informes semanales (por implementar)");
                    case 5 -> System.out.println("Funcionalidad de modificación extemporánea de asignaciones (por implementar)");
                    case 0 -> System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 0);
        }
    }
}
