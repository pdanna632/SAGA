package model;

public class Designacion {
    private Arbitro arbitro;
    private Partido partido;
    private String rol; // "Central" o "Asistente"

    public Designacion(Arbitro arbitro, Partido partido, String rol) {
        this.arbitro = arbitro;
        this.partido = partido;
        this.rol = rol;
    }

    public Arbitro getArbitro() {
        return arbitro;
    }

    public Partido getPartido() {
        return partido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Arbitro: " + arbitro.getNombre() + " (" + rol + ")\n" + partido.toString();
    }
}
