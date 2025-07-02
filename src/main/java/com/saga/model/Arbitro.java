package com.saga.model;

import com.saga.model.Disponibilidad;
import java.util.ArrayList;
import java.util.List;

public class Arbitro {
    private String nombre;
    private String cedula;
    private String telefono;
    private String categoria;
    private boolean activo;
    private List<Disponibilidad> disponibilidades;

    public Arbitro(String nombre, String cedula, String telefono, String categoria, boolean activo) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.categoria = categoria;
        this.activo = activo;
        this.disponibilidades = new ArrayList<>();
    }

    public void agregarDisponibilidad(Disponibilidad disp) {
        disponibilidades.add(disp);
    }

    public List<Disponibilidad> getDisponibilidades() {
        return disponibilidades;
    }

    @Override
    public String toString() {
        return nombre + " (C.C: " + cedula + ", Tel: " + telefono + ", Categoría: " + categoria + ", Activo: " + (activo ? "Sí" : "No") + ")";
    }

    // Getters (opcionales)
    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isActivo() {
        return activo;
    }
}
