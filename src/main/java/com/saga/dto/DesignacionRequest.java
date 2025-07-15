package com.saga.dto;

public class DesignacionRequest {
    private String arbitroNombre;
    private String partidoId;
    private String rol; // "Central" o "Asistente"

    public String getArbitroNombre() {
        return arbitroNombre;
    }

    public void setArbitroNombre(String arbitroNombre) {
        this.arbitroNombre = arbitroNombre;
    }

    public String getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(String partidoId) {
        this.partidoId = partidoId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
