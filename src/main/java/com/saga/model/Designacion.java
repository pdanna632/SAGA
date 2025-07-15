package com.saga.model;

public class Designacion implements java.io.Serializable {
    private Arbitro arbitro;
    private Partido partido;
    private String rol; // "Central" o "Asistente"
    private boolean realizado;

    public Designacion(Arbitro arbitro, Partido partido, String rol) {
        this(arbitro, partido, rol, true); // Cambiado a true por defecto
    }

    public Designacion(Arbitro arbitro, Partido partido, String rol, boolean realizado) {
        this.arbitro = arbitro;
        this.partido = partido;
        this.rol = rol;
        this.realizado = realizado;
    }

    public Designacion() {
        // Constructor vacío requerido para deserialización
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

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    @Override
    public String toString() {
        return "Arbitro: " + arbitro.getNombre() + " (" + rol + ")\n" + partido.toString() + "\nRealizado: " + (realizado ? "Sí" : "No");
    }
}
