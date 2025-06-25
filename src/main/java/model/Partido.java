package model;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Partido {
    private String categoria;
    private String municipio;
    private String equipoLocal;
    private String equipoVisitante;
    private LocalDate fecha;
    private LocalTime hora;
    private String escenario;
    private String id;

    public Partido(String categoria, String municipio, String equipoLocal, String equipoVisitante,
                   LocalDate fecha, LocalTime hora, String escenario) {
        this(categoria, municipio, equipoLocal, equipoVisitante, fecha, hora, escenario, UUID.randomUUID().toString());
    }

    public Partido(String categoria, String municipio, String equipoLocal, String equipoVisitante,
                   LocalDate fecha, LocalTime hora, String escenario, String id) {
        this.categoria = categoria;
        this.municipio = municipio;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fecha = fecha;
        this.hora = hora;
        this.escenario = escenario;
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getEscenario() {
        return escenario;
    }

    public String getId() {
        return id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setEscenario(String escenario) {
        this.escenario = escenario;
    }

    @Override
    public String toString() {
        return "Partido: " + equipoLocal + " vs " + equipoVisitante +
               "\nCategoría: " + categoria +
               "\nMunicipio: " + municipio +
               "\nFecha: " + fecha + " a las " + hora +
               "\nEscenario: " + escenario +
               "\nID: " + id;
    }
}
