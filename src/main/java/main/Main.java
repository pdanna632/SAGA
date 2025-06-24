package main;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime; 
import java.util.List;
import java.util.Scanner;

import model.Arbitro;
import model.Disponibilidad;
import utils.ExcelArbitroReader;
import utils.ExcelDisponibilidadReader;
import utils.ExcelDisponibilidadWriter;

public class Main {
    private static final boolean TESTING_MODE = true; // Cambiar a 'false' para desactivar el modo de prueba

    public static void main(String[] args) {
        System.out.println("===== Bienvenido a SAGA - Sistema Automatizado de Gestión Arbitral =====");

        // Crear objetos desde los archivos Excel
        List<Arbitro> arbitros = cargarArbitros();
        Disponibilidad disponibilidad = cargarDisponibilidades();

        if (!TESTING_MODE) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Ingrese el usuario: ");
                String usuario = scanner.nextLine();
                System.out.print("Ingrese la contraseña: ");
                String contrasena = scanner.nextLine();

                if (!usuario.equals("ARBIANTIOQUIA") || !contrasena.equals("ADMIN")) {
                    System.out.println("Credenciales incorrectas. Acceso denegado.");
                    return;
                }
            }
        } else {
            System.out.println("Modo de prueba activado. Saltando validación de credenciales...");
        }

        int opcion;
        try (Scanner scanner = new Scanner(System.in)) {
            do {
                System.out.println("\n===== SAGA - Sistema Automatizado de Gestión Arbitral =====");
                System.out.println("1. Visualización de árbitros disponibles");
                System.out.println("2. Asignación de árbitros a partidos o eventos (por implementar)");
                System.out.println("3. Generación de informes semanales (por implementar)");
                System.out.println("4. Modificación extemporánea de asignaciones (por implementar)");
                System.out.println("5. Modificar disponibilidad");
                System.out.println("6. Extras");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1 -> mostrarArbitrosDisponibles(arbitros);
                    case 2 -> System.out.println("Funcionalidad de asignación de árbitros (por implementar)");
                    case 3 -> System.out.println("Funcionalidad de generación de informes semanales (por implementar)");
                    case 4 -> System.out.println("Funcionalidad de modificación extemporánea de asignaciones (por implementar)");
                    case 5 -> {
                        System.out.print("\nIngrese la cédula del árbitro que desea modificar: ");
                        String cedula = scanner.nextLine();

                        Arbitro arbitro = arbitros.stream()
                                .filter(a -> a.getCedula().equals(cedula))
                                .findFirst()
                                .orElse(null);

                        if (arbitro == null) {
                            System.out.println("No se encontró un árbitro con la cédula proporcionada.");
                        } else {
                            modificarDisponibilidad(arbitro); // Llamada simplificada
                        }
                    }
                    case 6 -> mostrarExtras(arbitros, disponibilidad);
                    case 0 -> {
                        System.out.println("Guardando cambios en las disponibilidades...");
                        String rutaDisponibilidades = calcularRutaDisponibilidades();
                        ExcelDisponibilidadWriter.actualizarDisponibilidades(rutaDisponibilidades, arbitros); // Pasar la lista de árbitros
                        System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    }
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 0);
        }
    }

    private static String calcularRutaDisponibilidades() {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate ultimoDiaSemana = hoy.with(DayOfWeek.SUNDAY);
        String nombreArchivo = String.format("disponibilidades_%02d %02d a %02d %d.xlsx",
                primerDiaSemana.getMonthValue(),
                primerDiaSemana.getDayOfMonth(),
                ultimoDiaSemana.getDayOfMonth(),
                primerDiaSemana.getYear());
        return "src/main/resources/data/disponibilidades/" + nombreArchivo;
    }

    private static List<Arbitro> cargarArbitros() {
        System.out.println("\n===== Cargando árbitros desde el archivo Excel =====");
        String rutaArbitros = "src/main/resources/data/Arbitros.xlsx";
        List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros(rutaArbitros);

        if (arbitros.isEmpty()) {
            System.out.println("No se encontraron árbitros en el archivo.");
        } else {
            System.out.println("Árbitros cargados correctamente.");
        }

        return arbitros;
    }

    private static Disponibilidad cargarDisponibilidades() {
        System.out.println("\n===== Cargando disponibilidades desde el archivo Excel =====");

        String rutaDisponibilidades = calcularRutaDisponibilidades();
        File archivoDisponibilidades = new File(rutaDisponibilidades);

        if (!archivoDisponibilidades.exists()) {
            System.out.println("El archivo de disponibilidades no existe. Generando...");
            List<Arbitro> arbitros = cargarArbitros(); // Cargar árbitros para generar el archivo
            ExcelDisponibilidadWriter.generarArchivoDisponibilidades(arbitros, "src/main/resources/data/disponibilidades");
        }

        Disponibilidad disponibilidad = ExcelDisponibilidadReader.leerDisponibilidades(rutaDisponibilidades);

        if (disponibilidad == null) {
            System.out.println("No se encontraron disponibilidades en el archivo.");
        } else {
            System.out.println("Disponibilidades cargadas correctamente.");
        }

        return disponibilidad;
    }

    private static void mostrarArbitrosDisponibles(List<Arbitro> arbitros) {
        System.out.println("\n===== Árbitros disponibles =====");

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

    private static void modificarDisponibilidad(List<Arbitro> arbitros, String rutaArchivo) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nIngrese la cédula del árbitro que desea modificar: ");
            String cedula = scanner.nextLine();

            Arbitro arbitro = arbitros.stream()
                    .filter(a -> a.getCedula().equals(cedula))
                    .findFirst()
                    .orElse(null);

            if (arbitro == null) {
                System.out.println("No se encontró un árbitro con la cédula proporcionada.");
                continue;
            }

            modificarDisponibilidad(arbitro);

            System.out.print("¿Desea modificar otro árbitro? (y/n): ");
            String respuesta = scanner.nextLine();
            if (!respuesta.equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    private static void modificarDisponibilidad(Arbitro arbitro) {
        Scanner scanner = new Scanner(System.in);
        String rutaArchivo = "src/main/resources/data/disponibilidades"; // Ruta fija del archivo Excel

        System.out.println("Modificando disponibilidad para el árbitro: " + arbitro.getNombre());

        while (true) {
            System.out.println("\nSeleccione el día que desea modificar:");
            System.out.println("1. Jueves");
            System.out.println("2. Viernes");
            System.out.println("3. Sábado");
            System.out.println("4. Domingo");
            System.out.print("Seleccione una opción: ");
            int diaOpcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            String dia;
            switch (diaOpcion) {
                case 1 -> dia = "Jueves";
                case 2 -> dia = "Viernes";
                case 3 -> dia = "Sábado";
                case 4 -> dia = "Domingo";
                default -> {
                    System.out.println("Opción no válida. Intente de nuevo.");
                    continue;
                }
            }

            System.out.print("Ingrese el nuevo horario de inicio (HH:mm): ");
            String horaInicioStr = scanner.nextLine();
            System.out.print("Ingrese el nuevo horario de fin (HH:mm): ");
            String horaFinStr = scanner.nextLine();

            try {
                LocalTime horaInicio = LocalTime.parse(horaInicioStr);
                LocalTime horaFin = LocalTime.parse(horaFinStr);

                // Modificar la disponibilidad en el objeto Arbitro
                Disponibilidad disponibilidad = arbitro.getDisponibilidades().stream()
                        .filter(d -> d.getDisponibilidadSemanal().containsKey(dia))
                        .findFirst()
                        .orElse(null);

                if (disponibilidad == null) {
                    disponibilidad = new Disponibilidad(arbitro.getCedula());
                    arbitro.agregarDisponibilidad(disponibilidad);
                }

                disponibilidad.eliminarDisponibilidad(dia, horaInicio, horaFin); // Eliminar la disponibilidad anterior
                disponibilidad.agregarDisponibilidad(dia, horaInicio, horaFin); // Agregar la nueva disponibilidad

                System.out.println("Disponibilidad modificada correctamente.");
            } catch (Exception e) {
                System.out.println("Error al modificar la disponibilidad: " + e.getMessage());
            }

            System.out.print("¿Desea modificar otro día para este árbitro? (y/n): ");
            String respuesta = scanner.nextLine();
            if (!respuesta.equalsIgnoreCase("y")) {
                break;
            }
        }

        System.out.print("¿Desea guardar los cambios en el archivo Excel? (y/n): ");
        String confirmacion = scanner.nextLine();
        if (confirmacion.equalsIgnoreCase("y")) {
            ExcelDisponibilidadWriter.actualizarDisponibilidadArbitro(rutaArchivo, arbitro);
            System.out.println("Cambios guardados en el archivo Excel.");
        } else {
            System.out.println("Los cambios no se guardaron en el archivo Excel.");
        }
    }

    private static void mostrarExtras(List<Arbitro> arbitros, Disponibilidad disponibilidad) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n===== Extras =====");
        System.out.println("1. Mostrar información de un árbitro");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        if (opcion == 1) {
            mostrarArbitro(arbitros, disponibilidad);
        } else {
            System.out.println("Opción no válida.");
        }
    }

    private static void mostrarArbitro(List<Arbitro> arbitros, Disponibilidad disponibilidad) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nIngrese la cédula del árbitro que desea consultar: ");
        String cedula = scanner.nextLine();

        Arbitro arbitro = arbitros.stream()
                .filter(a -> a.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);

        if (arbitro == null) {
            System.out.println("No se encontró un árbitro con la cédula proporcionada.");
            return;
        }

        System.out.println("\nInformación del árbitro:");
        System.out.println("Nombre: " + arbitro.getNombre());
        System.out.println("Categoría: " + arbitro.getCategoria());
        System.out.println("Activo: " + (arbitro.isActivo() ? "Sí" : "No"));

        System.out.println("\nDisponibilidad:");
        boolean tieneDisponibilidad = false;
        for (String dia : disponibilidad.getDisponibilidadSemanal().keySet()) {
            List<Disponibilidad.FranjaHoraria> franjas = disponibilidad.consultarDisponibilidad(dia);
            if (!franjas.isEmpty()) {
                tieneDisponibilidad = true;
                System.out.print(dia + ": ");
                franjas.forEach(franja -> System.out.print(franja.getInicio() + " a " + franja.getFin() + ", "));
                System.out.println();
            }
        }

        if (!tieneDisponibilidad) {
            System.out.println("No tiene disponibilidad.");
        }
    }
}
