import java.time.LocalDate;
import java.time.LocalTime;
import model.Arbitro;
import model.Disponibilidad;


public class Main {
    public static void main(String[] args) {
        Arbitro arbitro = new Arbitro("Samuel Gutierrez", "1023626538", "3043583429");

        Disponibilidad d1 = new Disponibilidad(
            LocalDate.of(2025, 6, 26),
            LocalTime.of(15, 0),
            LocalTime.of(18, 0)
        );

        arbitro.agregarDisponibilidad(d1);

        System.out.println(arbitro);
        for (Disponibilidad d : arbitro.getDisponibilidades()) {
            System.out.println("Disponible: " + d);
        }
    }
}
