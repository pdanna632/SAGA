package com.saga.model;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime; 
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.saga.utils.ExcelArbitroReader;
import com.saga.utils.ExcelDisponibilidadWriter;
import com.saga.utils.ExcelPartidoReader;

public class Main {
    private static final boolean TESTING_MODE = false; // Cambiar a 'false' para desactivar el modo de prueba
    public static List<Designacion> designacionesEnMemoria;

    public static void main(String[] args) {
        System.out.println("\n==================================================");
        System.out.println("         SAGA - SISTEMA DE GESTIÓN ARBITRAL       ");
        System.out.println("==================================================\n");

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
        Map<String, Disponibilidad> mapaDisponibilidades = com.saga.utils.ExcelDisponibilidadReader.leerTodasLasDisponibilidades(rutaDisponibilidades);
        for (Arbitro arbitro : arbitros) {
            Disponibilidad disp = mapaDisponibilidades.get(arbitro.getCedula());
            if (disp != null) {
                arbitro.getDisponibilidades().clear();
                arbitro.agregarDisponibilidad(disp);
            }
        }

        // Crear archivo de designaciones semanal si no existe
        String rutaDesignaciones = com.saga.utils.ExcelDesignacionWriterReader.calcularRutaDesignaciones();
        File archivoDesignaciones = new File(rutaDesignaciones);
        if (!archivoDesignaciones.exists()) {
            com.saga.utils.ExcelDesignacionWriterReader.guardarDesignaciones(new java.util.ArrayList<>());
            System.out.println("Archivo de designaciones creado: " + rutaDesignaciones);
        }

        // Cargar designaciones en memoria al iniciar
        designacionesEnMemoria = com.saga.utils.ExcelDesignacionWriterReader.leerDesignaciones(partidos, arbitros);

        try (Scanner scanner = new Scanner(System.in)) {
            if (!TESTING_MODE) {
                System.out.println("------------------- INICIO DE SESIÓN -------------------");
                System.out.print("Usuario: ");
                String usuario = scanner.nextLine();
                System.out.print("Contraseña: ");
                String contrasena = scanner.nextLine();
                System.out.println("--------------------------------------------------------");
                if (!usuario.equals("ARBIANTIOQUIA") || !contrasena.equals("ADMIN")) {
                    System.out.println("\n[!] Credenciales incorrectas. Acceso denegado.\n");
                    return;
                }
            } else {
                System.out.println("[MODO PRUEBA] Saltando validación de credenciales...\n");
            }
            int opcion;
            do {
                System.out.println("\n==================================================");
                System.out.println("         MENÚ PRINCIPAL SAGA - GESTIÓN ARBITRAL   ");
                System.out.println("==================================================");
                System.out.println(" 1. Visualización de árbitros disponibles");
                System.out.println(" 2. Asignación de árbitros a partidos o eventos");
                System.out.println(" 3. Generación de informes semanales");
                System.out.println(" 4. Modificación extemporánea de asignaciones");
                System.out.println(" 5. Extras");
                System.out.println(" 0. Salir");
                System.out.println("--------------------------------------------------");
                System.out.print("Seleccione una opción: ");
                try {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); // Limpiar buffer
                } catch (NoSuchElementException | IllegalStateException e) {
                    System.out.println("No se pudo leer la opción del menú. ¿Está ejecutando el programa en un entorno sin entrada estándar? Saliendo...");
                    break;
                }

                switch (opcion) {
                    case 1 -> mostrarArbitrosDisponibles(arbitros, partidos, scanner);
                    case 2 -> menuAsignacionArbitros(arbitros, partidos);
                    case 3 -> generarInformeSemanal(partidos, arbitros);
                    case 4 -> menuModificacionExtemporanea(arbitros, partidos);
                    case 5 -> mostrarExtras(arbitros, partidos, scanner);
                    case 0 -> {
                        System.out.println("Guardando cambios en las disponibilidades...");
                        String ruta = calcularRutaDisponibilidades();
                        com.saga.utils.ExcelDisponibilidadWriter.actualizarDisponibilidades(ruta, arbitros);
                        System.out.println("Guardando cambios en los partidos...");
                        com.saga.utils.ExcelPartidoWriter.escribirPartidos("src/main/resources/data/Partidos.xlsx", partidos);
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

    private static void mostrarArbitrosDisponibles(List<Arbitro> arbitros, List<Partido> partidos, Scanner scanner) {
        System.out.println("\n------------------ CONSULTA DE ÁRBITROS DISPONIBLES ------------------");
        System.out.println("  1. Buscar por día y hora");
        System.out.println("  2. Buscar por partido");
        System.out.println("---------------------------------------------------------------------");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        String dia = null;
        LocalTime hora = null;
        if (opcion == 1) {
            System.out.println("Seleccione el día:");
            System.out.println("  1. Jueves");
            System.out.println("  2. Viernes");
            System.out.println("  3. Sábado");
            System.out.println("  4. Domingo");
            System.out.print("Opción: ");
            int diaOpcion = scanner.nextInt();
            scanner.nextLine();
            switch (diaOpcion) {
                case 1 -> dia = "Jueves";
                case 2 -> dia = "Viernes";
                case 3 -> dia = "Sábado";
                case 4 -> dia = "Domingo";
                default -> {
                    System.out.println("[!] Opción de día no válida.");
                    return;
                }
            }
            System.out.print("Ingrese la hora de inicio (formato HH:mm, ej: 12:00): ");
            String horaStr = scanner.nextLine().trim();
            try {
                hora = LocalTime.parse(horaStr.length() == 4 ? "0" + horaStr : horaStr);
            } catch (Exception e) {
                System.out.println("[!] Hora inválida. Debe ser en formato HH:mm");
                return;
            }
        } else if (opcion == 2) {
            if (partidos.isEmpty()) {
                System.out.println("[!] No hay partidos cargados.");
                return;
            }
            System.out.println("\n------------------ PARTIDOS DISPONIBLES ------------------");
            for (int i = 0; i < partidos.size(); i++) {
                System.out.printf("%2d. %-40s\n", i + 1, partidos.get(i));
            }
            System.out.println("----------------------------------------------------------");
            System.out.print("Seleccione el número del partido: ");
            int idx = scanner.nextInt() - 1;
            scanner.nextLine();
            if (idx < 0 || idx >= partidos.size()) {
                System.out.println("[!] Selección inválida.");
                return;
            }
            Partido partido = partidos.get(idx);
            String[] diasES = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            String diaSemana = diasES[partido.getFecha().getDayOfWeek().getValue() - 1];
            dia = diaSemana;
            hora = partido.getHora();
            System.out.println("\nBuscando árbitros disponibles para el partido: " + partido);
        } else {
            System.out.println("[!] Opción no válida.");
            return;
        }
        boolean alguno = false;
        System.out.println("\n================= ÁRBITROS DISPONIBLES =================");
        System.out.printf("%-15s %-25s %-10s\n", "CÉDULA", "NOMBRE", "CATEGORÍA");
        System.out.println("--------------------------------------------------------");
        for (Arbitro a : arbitros) {
            if (!a.isActivo()) continue;
            for (Disponibilidad disp : a.getDisponibilidades()) {
                for (Disponibilidad.FranjaHoraria franja : disp.consultarDisponibilidad(dia)) {
                    if (!hora.isBefore(franja.getInicio()) && hora.isBefore(franja.getFin())) {
                        System.out.printf("%-15s %-25s %-10s\n", a.getCedula(), a.getNombre(), a.getCategoria());
                        alguno = true;
                        break;
                    }
                }
            }
        }
        if (!alguno) {
            System.out.println("[!] No hay árbitros disponibles en ese día y hora.");
        }
        System.out.println("========================================================\n");
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

            System.out.print("¿Desea modificar otro árbitro? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (!respuesta.equals("s") && !respuesta.equals("si") && !respuesta.equals("sí") && !respuesta.equals("1")) {
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
            System.out.println(" 1. Jueves");
            System.out.println(" 2. Viernes");
            System.out.println(" 3. Sábado");
            System.out.println(" 4. Domingo");
            System.out.print("Opción: ");
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

            System.out.print("¿Desea modificar otro día para este árbitro? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (!respuesta.equals("s") && !respuesta.equals("si") && !respuesta.equals("sí") && !respuesta.equals("1")) {
                break;
            }
        }

        System.out.print("¿Desea guardar los cambios en el archivo Excel? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (confirmacion.equals("s") || confirmacion.equals("si") || confirmacion.equals("sí") || confirmacion.equals("1")) {
            ExcelDisponibilidadWriter.actualizarDisponibilidadArbitro(rutaArchivo, arbitro);
            System.out.println("Cambios guardados en el archivo Excel.");
        } else {
            System.out.println("Los cambios no se guardaron en el archivo Excel.");
        }
    }

    private static void mostrarExtras(List<Arbitro> arbitros, List<Partido> partidos, Scanner scanner) {
        System.out.println("\n========================= EXTRAS =========================");
        System.out.println(" 1. Mostrar información de un árbitro");
        System.out.println(" 2. Ver partidos cargados");
        System.out.println(" 3. Iniciar bot de WhatsApp");
        System.out.println("---------------------------------------------------------");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        switch (opcion) {
            case 1 -> mostrarArbitro(arbitros, scanner);
            case 2 -> mostrarPartidos(partidos);
            case 3 -> iniciarBotWhatsApp();
            default -> System.out.println("[!] Opción no válida.");
        }
        System.out.println("========================================================\n");
    }

    private static void mostrarPartidos(List<Partido> partidos) {
        System.out.println("\n==================== PARTIDOS CARGADOS ====================");
        if (partidos.isEmpty()) {
            System.out.println("[!] No hay partidos cargados.");
            return;
        }
        System.out.printf("%-3s %-25s %-15s %-10s %-20s\n", "#", "PARTIDO", "FECHA", "HORA", "ESCENARIO");
        System.out.println("----------------------------------------------------------");
        for (int i = 0; i < partidos.size(); i++) {
            Partido p = partidos.get(i);
            System.out.printf("%2d. %-25s %-15s %-10s %-20s\n", i + 1,
                p.getEquipoLocal() + " vs " + p.getEquipoVisitante(),
                p.getFecha(),
                p.getHora(),
                p.getEscenario());
        }
        System.out.println("==========================================================\n");
    }

    private static void mostrarArbitro(List<Arbitro> arbitros, Scanner scanner) {
        System.out.println("\n------------------ CONSULTA DE ÁRBITRO ------------------");
        System.out.print("Ingrese la cédula del árbitro que desea consultar: ");
        String cedula = scanner.nextLine();
        Arbitro arbitro = arbitros.stream()
                .filter(a -> a.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
        if (arbitro == null) {
            System.out.println("[!] No se encontró un árbitro con la cédula proporcionada.");
            return;
        }
        System.out.println("\n================= INFORMACIÓN DEL ÁRBITRO ================");
        System.out.printf("%-15s: %s\n", "Nombre", arbitro.getNombre());
        System.out.printf("%-15s: %s\n", "Cédula", arbitro.getCedula());
        System.out.printf("%-15s: %s\n", "Categoría", arbitro.getCategoria());
        System.out.printf("%-15s: %s\n", "Activo", arbitro.isActivo() ? "Sí" : "No");
        System.out.println("----------------------------------------------------------");
        System.out.println("Disponibilidad semanal:");
        boolean tieneDisponibilidad = false;
        for (Disponibilidad disp : arbitro.getDisponibilidades()) {
            for (String dia : disp.getDisponibilidadSemanal().keySet()) {
                List<Disponibilidad.FranjaHoraria> franjas = disp.consultarDisponibilidad(dia);
                if (!franjas.isEmpty()) {
                    tieneDisponibilidad = true;
                    System.out.printf("%-10s: ", dia);
                    for (Disponibilidad.FranjaHoraria franja : franjas) {
                        System.out.print(franja + ", ");
                    }
                    System.out.println();
                }
            }
        }
        if (!tieneDisponibilidad) {
            System.out.println("No tiene disponibilidad registrada.");
        }
        System.out.println("==========================================================\n");
    }

    private static void menuModificacionExtemporanea(List<Arbitro> arbitros, List<Partido> partidos) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Modificación Extemporánea =====");
        System.out.println("1. Modificar disponibilidad de árbitro");
        System.out.println("2. Modificar o eliminar partido");
        System.out.println("3. Marcar evento como NO realizado");
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
            case 3 -> marcarEventoNoRealizado(partidos, arbitros);
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void marcarEventoNoRealizado(List<Partido> partidos, List<Arbitro> arbitros) {
        List<Designacion> designaciones = com.saga.utils.ExcelDesignacionWriterReader.leerDesignaciones(partidos, arbitros);
        if (designaciones.isEmpty()) {
            System.out.println("No hay designaciones para la semana actual.");
            return;
        }
        System.out.println("\n===== Designaciones de la semana actual =====");
        for (int i = 0; i < designaciones.size(); i++) {
            Designacion d = designaciones.get(i);
            System.out.printf("%d. %s | %s | %s | %s | Realizado: %s\n", i + 1,
                d.getPartido().getEquipoLocal() + " vs " + d.getPartido().getEquipoVisitante(),
                d.getPartido().getFecha(),
                d.getPartido().getHora(),
                d.getArbitro().getNombre() + " (" + d.getRol() + ")",
                d.isRealizado() ? "Sí" : "No");
        }
        System.out.print("Seleccione el número de la designación a marcar como NO realizada (0 para cancelar): ");
        Scanner inputScanner = new Scanner(System.in);
        int idx = inputScanner.nextInt() - 1;
        if (idx < 0 || idx >= designaciones.size()) {
            System.out.println("Operación cancelada.");
            return;
        }
        designaciones.get(idx).setRealizado(false);
        com.saga.utils.ExcelDesignacionWriterReader.guardarDesignaciones(designaciones);
        System.out.println("Designación actualizada como NO realizada.");
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
        com.saga.utils.ExcelPartidoWriter.escribirPartidos("src/main/resources/data/Partidos.xlsx", partidos);
        System.out.println("Cambios guardados en el archivo Excel.");
    }

    private static void menuAsignacionArbitros(List<Arbitro> arbitros, List<Partido> partidos) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== Asignación de árbitros a partidos =====");
        System.out.println("1. Asignar manualmente (por cédula y partido)");
        System.out.println("2. Asignar seleccionando partido y árbitro disponible");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        if (opcion == 1) {
            System.out.print("Ingrese la cédula del árbitro: ");
            String cedula = scanner.nextLine();
            Arbitro arbitro = arbitros.stream().filter(a -> a.getCedula().equals(cedula) && a.isActivo()).findFirst().orElse(null);
            if (arbitro == null) {
                System.out.println("No se encontró un árbitro activo con esa cédula.");
                return;
            }
            for (int i = 0; i < partidos.size(); i++) {
                System.out.println((i + 1) + ". " + partidos.get(i));
            }
            System.out.print("Seleccione el número del partido: ");
            int idx = scanner.nextInt() - 1;
            scanner.nextLine();
            if (idx < 0 || idx >= partidos.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            Partido partido = partidos.get(idx);
            if (!arbitroDisponibleParaPartido(arbitro, partido)) {
                System.out.println("El árbitro no está disponible en la fecha y hora del partido.");
                return;
            }
            System.out.println("Seleccione el rol a asignar:");
            System.out.println(" 1. Central");
            System.out.println(" 2. Asistente");
            System.out.print("Opción: ");
            int rolOpcion = scanner.nextInt();
            scanner.nextLine();
            String rol;
            if (rolOpcion == 1) rol = "Central";
            else if (rolOpcion == 2) rol = "Asistente";
            else {
                System.out.println("Rol no válido.");
                return;
            }
            Designacion designacion = new Designacion(arbitro, partido, rol);
            // Guardar inmediatamente la designación en el Excel semanal
            List<Designacion> designaciones = com.saga.utils.ExcelDesignacionWriterReader.leerDesignaciones(partidos, arbitros);
            designaciones.add(designacion);
            com.saga.utils.ExcelDesignacionWriterReader.guardarDesignaciones(designaciones);
            System.out.println("Designación creada: " + designacion);
        } else if (opcion == 2) {
            if (partidos.isEmpty()) {
                System.out.println("No hay partidos cargados.");
                return;
            }
            for (int i = 0; i < partidos.size(); i++) {
                System.out.println((i + 1) + ". " + partidos.get(i));
            }
            System.out.print("Seleccione el número del partido: ");
            int idx = scanner.nextInt() - 1;
            scanner.nextLine();
            if (idx < 0 || idx >= partidos.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            Partido partido = partidos.get(idx);
            // Mostrar árbitros disponibles
            List<Arbitro> disponibles = arbitros.stream().filter(a -> a.isActivo() && arbitroDisponibleParaPartido(a, partido)).toList();
            if (disponibles.isEmpty()) {
                System.out.println("No hay árbitros disponibles para este partido.");
                return;
            }
            for (int i = 0; i < disponibles.size(); i++) {
                System.out.println((i + 1) + ". " + disponibles.get(i));
            }
            System.out.print("Seleccione el número del árbitro a asignar: ");
            int idxArb = scanner.nextInt() - 1;
            scanner.nextLine();
            if (idxArb < 0 || idxArb >= disponibles.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            Arbitro arbitro = disponibles.get(idxArb);
            System.out.println("Seleccione el rol a asignar:");
            System.out.println(" 1. Central");
            System.out.println(" 2. Asistente");
            System.out.print("Opción: ");
            int rolOpcion = scanner.nextInt();
            scanner.nextLine();
            String rol;
            if (rolOpcion == 1) rol = "Central";
            else if (rolOpcion == 2) rol = "Asistente";
            else {
                System.out.println("Rol no válido.");
                return;
            }
            Designacion designacion = new Designacion(arbitro, partido, rol);
            List<Designacion> designaciones = com.saga.utils.ExcelDesignacionWriterReader.leerDesignaciones(partidos, arbitros);
            designaciones.add(designacion);
            com.saga.utils.ExcelDesignacionWriterReader.guardarDesignaciones(designaciones);
            System.out.println("Designación creada: " + designacion);
        } else {
            System.out.println("Opción no válida.");
        }
    }

    private static boolean arbitroDisponibleParaPartido(Arbitro arbitro, Partido partido) {
        String[] diasES = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        String dia = diasES[partido.getFecha().getDayOfWeek().getValue() - 1];
        LocalTime hora = partido.getHora();
        for (Disponibilidad disp : arbitro.getDisponibilidades()) {
            for (Disponibilidad.FranjaHoraria franja : disp.consultarDisponibilidad(dia)) {
                if (!hora.isBefore(franja.getInicio()) && hora.isBefore(franja.getFin())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void generarInformeSemanal(List<Partido> partidos, List<Arbitro> arbitros) {
        List<Designacion> designaciones = com.saga.utils.ExcelDesignacionWriterReader.leerDesignaciones(partidos, arbitros);
        System.out.println("\n================= INFORME SEMANAL DE EVENTOS REALIZADOS =================");
        System.out.printf("%-25s %-20s %-10s %-8s %-12s %-20s %-10s\n", "PARTIDO", "ESCENARIO", "FECHA", "HORA", "ROL", "ÁRBITRO", "REALIZADO");
        System.out.println("-------------------------------------------------------------------------------");
        for (Designacion d : designaciones) {
            if (d.isRealizado()) {
                Partido p = d.getPartido();
                System.out.printf("%-25s %-20s %-10s %-8s %-12s %-20s %-10s\n",
                    p.getEquipoLocal() + " vs " + p.getEquipoVisitante(),
                    p.getEscenario(),
                    p.getFecha(),
                    p.getHora(),
                    d.getRol(),
                    d.getArbitro().getNombre(),
                    d.isRealizado() ? "Sí" : "No");
            }
        }
        System.out.println("===============================================================================\n");
    }

    /**
     * Método para iniciar el bot de WhatsApp en una nueva ventana de CMD
     */
    private static void iniciarBotWhatsApp() {
        try {
            System.out.println("\n🤖 Iniciando bot de WhatsApp SAGA...");
            
            // Ruta al directorio del bot
            String rutaBot = "whatsapp-bot";
            String rutaCompleta = System.getProperty("user.dir") + File.separator + rutaBot;
            
            // Verificar que el directorio existe
            File directorioBot = new File(rutaCompleta);
            if (!directorioBot.exists()) {
                System.out.println("❌ Error: No se encontró el directorio del bot en: " + rutaCompleta);
                System.out.println("   Asegúrate de que la carpeta 'whatsapp-bot' esté en el directorio raíz del proyecto.");
                return;
            }
            
            // Verificar que package.json existe
            File packageJson = new File(rutaCompleta + File.separator + "package.json");
            if (!packageJson.exists()) {
                System.out.println("❌ Error: No se encontró package.json en el directorio del bot.");
                System.out.println("   Ejecuta primero la instalación del bot.");
                return;
            }
            
            // Verificar que el script de inicio existe
            File scriptInicio = new File(rutaCompleta + File.separator + "iniciar-bot.bat");
            if (!scriptInicio.exists()) {
                System.out.println("❌ Error: No se encontró el script iniciar-bot.bat en el directorio del bot.");
                System.out.println("   Asegúrate de que todos los archivos del bot estén presentes.");
                return;
            }
            
            // Comando para abrir nueva ventana de CMD y ejecutar el bot
            String[] comandos = {
                "cmd.exe", 
                "/c", 
                "start", 
                "SAGA WhatsApp Bot", 
                "cmd.exe", 
                "/k", 
                "cd /d \"" + rutaCompleta + "\" && iniciar-bot.bat"
            };
            
            // Ejecutar el comando
            ProcessBuilder processBuilder = new ProcessBuilder(comandos);
            processBuilder.start();
            
            System.out.println("✅ Bot de WhatsApp iniciado en una nueva ventana de CMD.");
            System.out.println("📱 Sigue las instrucciones en la nueva ventana para conectar el bot.");
            System.out.println("🔄 El bot funcionará independientemente de esta aplicación.");
            System.out.println("💡 Para detener el bot, presiona Ctrl+C en la ventana del bot.");
            
        } catch (java.io.IOException e) {
            System.out.println("❌ Error al iniciar el bot de WhatsApp: " + e.getMessage());
            System.out.println("   Verifica que Node.js esté instalado y que el bot esté configurado correctamente.");
            System.out.println("   También puedes ejecutar manualmente el bot desde la carpeta whatsapp-bot con: npm start");
        } catch (SecurityException e) {
            System.out.println("❌ Error de seguridad al intentar ejecutar el bot: " + e.getMessage());
            System.out.println("   Ejecuta manualmente el bot desde la carpeta whatsapp-bot con: npm start");
        }
    }
}
