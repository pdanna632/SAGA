package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Disponibilidad {
    private Map<String, List<FranjaHoraria>> disponibilidadSemanal;

    public Disponibilidad() {
        disponibilidadSemanal = new HashMap<>();
        inicializarDias();
    }

    private void inicializarDias() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (String dia : dias) {
            disponibilidadSemanal.put(dia, new ArrayList<>());
        }
    }

    public void agregarDisponibilidad(String dia, LocalTime inicio, LocalTime fin) {
        if (!disponibilidadSemanal.containsKey(dia)) {
            throw new IllegalArgumentException("Día inválido: " + dia);
        }
        disponibilidadSemanal.get(dia).add(new FranjaHoraria(inicio, fin));
    }

    public void eliminarDisponibilidad(String dia, LocalTime inicio, LocalTime fin) {
        if (!disponibilidadSemanal.containsKey(dia)) {
            throw new IllegalArgumentException("Día inválido: " + dia);
        }
        disponibilidadSemanal.get(dia).removeIf(franja -> franja.getInicio().equals(inicio) && franja.getFin().equals(fin));
    }

    public List<FranjaHoraria> consultarDisponibilidad(String dia) {
        if (!disponibilidadSemanal.containsKey(dia)) {
            throw new IllegalArgumentException("Día inválido: " + dia);
        }
        return disponibilidadSemanal.get(dia);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<FranjaHoraria>> entry : disponibilidadSemanal.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            if (entry.getValue().isEmpty()) {
                sb.append("No disponible\n");
            } else {
                for (FranjaHoraria franja : entry.getValue()) {
                    sb.append(franja).append(", ");
                }
                sb.setLength(sb.length() - 2); // Eliminar la última coma
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static class FranjaHoraria {
        private LocalTime inicio;
        private LocalTime fin;

        public FranjaHoraria(LocalTime inicio, LocalTime fin) {
            this.inicio = inicio;
            this.fin = fin;
        }

        public LocalTime getInicio() {
            return inicio;
        }

        public LocalTime getFin() {
            return fin;
        }

        @Override
        public String toString() {
            return inicio + " a " + fin;
        }
    }
}
