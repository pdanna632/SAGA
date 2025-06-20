import java.time.LocalDate;
import java.time.LocalTime;

public class Disponibilidad {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Disponibilidad(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String toString() {
        return fecha + " de " + horaInicio + " a " + horaFin;
    }

    // Puedes agregar métodos como:
    public boolean seSuperponeCon(Disponibilidad otra) {
        return this.fecha.equals(otra.fecha) &&
               !(this.horaFin.isBefore(otra.horaInicio) || this.horaInicio.isAfter(otra.horaFin));
    }
}
