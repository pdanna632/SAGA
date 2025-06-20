import java.util.ArrayList;
import java.util.List;

public class Arbitro {
    private String nombre;
    private String cedula;
    private String telefono;
    private List<Disponibilidad> disponibilidades;

    public Arbitro(String nombre, String cedula, String telefono) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.disponibilidades = new ArrayList<>();
    }

    public void agregarDisponibilidad(Disponibilidad disp) {
        disponibilidades.add(disp);
    }

    public List<Disponibilidad> getDisponibilidades() {
        return disponibilidades;
    }

    public String toString() {
        return nombre + " (C.C: " + cedula + ", Tel: " + telefono + ")";
    }
}
