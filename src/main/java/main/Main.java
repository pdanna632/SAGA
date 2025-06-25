package main;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime; 
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Arbitro;
import model.Disponibilidad;
import model.Partido;
import utils.ExcelArbitroReader;
import utils.ExcelDisponibilidadWriter;
import utils.ExcelPartidoReader;

public class Main {
    private static final boolean TESTING_MODE = true; // Cambiar a 'false' para desactivar el modo de prueba

    public static void main(String[] args) {
        System.out.println("===== Bienvenido a SAGA - Sistema Automatizado de Gestión Arbitral =====");

        // Crear objetos desde los archivos Excel
        List<Arbitro> arbitros = cargarArbitros();
        List<Partido> partidos = ExcelPartidoReader.leerPartidos("src/main/resources/data/Partidos.xlsx");
        String rutaDisponibilidades = calcularRutaDisponibilidades();
        File archivoDisponibilidades = new File(rutaDisponibilidades);
        if (!archivoDisponibilidades.exists()) {
            System.out.println("El archivo de disponibilidades no existe. Generando...");
            ExcelDisponibilidadWriter.generarArchivoDisponibilidades(arbitros, "src/main/resources/data/disponibilidades");
        }
        // Leer todas las disponibilidades y asociarlas a cada árbitro
        Map<String, Disponibilidad> mapaDisponibilidades = utils.ExcelDisponibilidadReader.leerTodasLasDisponibilidades(rutaDisponibilidades);
        for (Arbitro arbitro : arbitros) {
            Disponibilidad disp = mapaDisponibilidades.get(arbitro.getCedula());
            if (disp != null) {
                arbitro.getDisponibilidades().clear();
                arbitro.agregarDisponibilidad(disp);
            }
        }

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
                System.out.println("4. Modificación extemporánea de asignaciones");
                System.out.println("5. Extras");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1 -> mostrarArbitrosDisponibles(arbitros);
                    case 2 -> System.out.println("Funcionalidad de asignación de árbitros (por implementar)");
                    case 3 -> System.out.println("Funcionalidad de generación de informes semanales (por implementar)");
                    case 4 -> menuModificacionExtemporanea(arbitros, partidos);
                    case 5 -> mostrarExtras(arbitros, partidos);
                    case 0 -> {
                        System.out.println("Guardando cambios en las disponibilidades...");
                        String ruta = calcularRutaDisponibilidades();
                        utils.ExcelDisponibilidadWriter.actualizarDisponibilidades(ruta, arbitros);
                        System.out.println("¡Hasta luego!");
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

    private static void mostrarArbitrosDisponibles(List<Arbitro> arbitros) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Consulta de árbitros disponibles =====");
        System.out.print("Ingrese el día (Jueves, Viernes, Sábado, Domingo): ");
        String dia = scanner.nextLine().trim();
        System.out.print("Ingrese la hora de inicio (formato HH:mm, ej: 12:00): ");
        String horaStr = scanner.nextLine().trim();
        LocalTime hora;
        try {
            hora = LocalTime.parse(horaStr.length() == 4 ? "0" + horaStr : horaStr);
        } catch (Exception e) {
            System.out.println("Hora inválida. Debe ser en formato HH:mm");
            return;
        }
        boolean alguno = false;
        for (Arbitro a : arbitros) {
            if (!a.isActivo()) continue;
            for (Disponibilidad disp : a.getDisponibilidades()) {
                for (Disponibilidad.FranjaHoraria franja : disp.consultarDisponibilidad(dia)) {
                    if (!hora.isBefore(franja.getInicio()) && hora.isBefore(franja.getFin())) {
                        System.out.println(a);
                        alguno = true;
                        break;
                    }
                }
            }
        }
        if (!alguno) {
            System.out.println("No hay árbitros disponibles en ese día y hora.");
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

    private static void mostrarExtras(List<Arbitro> arbitros, List<Partido> partidos) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Extras =====");
        System.out.println("1. Mostrar información de un árbitro");
        System.out.println("2. Ver partidos cargados");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        if (opcion == 1) {
            mostrarArbitro(arbitros);
        } else if (opcion == 2) {
            mostrarPartidos(partidos);
        } else {
            System.out.println("Opción no válida.");
        }
    }

    private static void mostrarPartidos(List<Partido> partidos) {
        System.out.println("\n===== Partidos cargados =====");
        if (partidos.isEmpty()) {
            System.out.println("No hay partidos cargados.");
            return;
        }
        for (Partido partido : partidos) {
            System.out.println(partido);
            System.out.println("----------------------");
        }
    }

    private static void mostrarArbitro(List<Arbitro> arbitros) {
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
        for (Disponibilidad disp : arbitro.getDisponibilidades()) {
            for (String dia : disp.getDisponibilidadSemanal().keySet()) {
                List<Disponibilidad.FranjaHoraria> franjas = disp.consultarDisponibilidad(dia);
                if (!franjas.isEmpty()) {
                    tieneDisponibilidad = true;
                    System.out.print(dia + ": ");
                    for (Disponibilidad.FranjaHoraria franja : franjas) {
                        System.out.print(franja + ", ");
                    }
                    System.out.println();
                }
            }
        }
        if (!tieneDisponibilidad) {
            System.out.println("No tiene disponibilidad.");
        }
    }

    private static void menuModificacionExtemporanea(List<Arbitro> arbitros, List<Partido> partidos) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Modificación Extemporánea =====");
        System.out.println("1. Modificar disponibilidad de árbitro");
        System.out.println("2. Modificar o eliminar partido");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        switch (opcion) {
            case 1 -> {
                System.out.print("\nIngrese la cédula del árbitro que desea modificar: ");
                String cedula = scanner.nextLine();
                Arbitro arbitro = arbitros.stream()
                        .filter(a -> a.getCedula().equals(cedula))
                        .findFirst()
                        .orElse(null);
                if (arbitro == null) {
                    System.out.println("No se encontró un árbitro con la cédula proporcionada.");
                } else {
                    modificarDisponibilidad(arbitro);
                }
            }
            case 2 -> modificarPartido(partidos);
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void modificarPartido(List<Partido> partidos) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Modificar o Eliminar Partido =====");
        for (int i = 0; i < partidos.size(); i++) {
            System.out.println((i + 1) + ". " + partidos.get(i));
        }
        System.out.print("Seleccione el número del partido a modificar/eliminar (0 para cancelar): ");
        int idx = scanner.nextInt() - 1;
        scanner.nextLine();
        if (idx < 0 || idx >= partidos.size()) {
            System.out.println("Operación cancelada.");
            return;
        }
        Partido partido = partidos.get(idx);
        System.out.println("1. Modificar información del partido");
        System.out.println("2. Eliminar partido");
        System.out.print("Seleccione una opción: ");
        int op = scanner.nextInt();
        scanner.nextLine();
        if (op == 1) {
            System.out.print("Nueva fecha (yyyy-MM-dd, enter para mantener): ");
            String nuevaFecha = scanner.nextLine();
            if (!nuevaFecha.isBlank()) partido.setFecha(LocalDate.parse(nuevaFecha));
            System.out.print("Nueva hora (HH:mm, enter para mantener): ");
            String nuevaHora = scanner.nextLine();
            if (!nuevaHora.isBlank()) partido.setHora(LocalTime.parse(nuevaHora));
            System.out.print("Nuevo escenario (enter para mantener): ");
            String nuevoEscenario = scanner.nextLine();
            if (!nuevoEscenario.isBlank()) partido.setEscenario(nuevoEscenario);
            System.out.println("Partido modificado.");
        } else if (op == 2) {
            partidos.remove(idx);
            System.out.println("Partido eliminado.");
        } else {
            System.out.println("Opción no válida.");
            return;
        }
        // Guardar cambios en el archivo Excel
        utils.ExcelPartidoWriter.escribirPartidos("src/main/resources/data/Partidos.xlsx", partidos);
        System.out.println("Cambios guardados en el archivo Excel.");
    }
}
