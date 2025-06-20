package model;


import java.time.LocalDate;
import java.time.LocalTime;

public class Partido {
    private String categoria;
    private String municipio;
    private String equipoLocal;
    private String equipoVisitante;
    private LocalDate fecha;
    private LocalTime hora;
    private String escenario;

    public Partido(String categoria, String municipio, String equipoLocal, String equipoVisitante,
                   LocalDate fecha, LocalTime hora, String escenario) {
        this.categoria = categoria;
        this.municipio = municipio;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fecha = fecha;
        this.hora = hora;
        this.escenario = escenario;
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

    @Override
    public String toString() {
        return "Partido: " + equipoLocal + " vs " + equipoVisitante +
               "\nCategoría: " + categoria +
               "\nMunicipio: " + municipio +
               "\nFecha: " + fecha + " a las " + hora +
               "\nEscenario: " + escenario;
    }
}
