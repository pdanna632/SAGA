package com.saga.model;

/**
 * Resultado de un proceso de autenticación
 */
public class AuthResult {
    
    public enum Status {
        AUTENTICADO,
        TELEFONO_NO_ENCONTRADO,
        CEDULA_NO_ENCONTRADA,
        ERROR_VALIDACION,
        ERROR_SISTEMA
    }
    
    private Status status;
    private Arbitro arbitro;
    private String mensaje;
    
    public AuthResult(Status status) {
        this.status = status;
    }
    
    public AuthResult(Status status, Arbitro arbitro) {
        this.status = status;
        this.arbitro = arbitro;
    }
    
    public AuthResult(Status status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }
    
    public AuthResult(Status status, Arbitro arbitro, String mensaje) {
        this.status = status;
        this.arbitro = arbitro;
        this.mensaje = mensaje;
    }
    
    // Getters y setters
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Arbitro getArbitro() {
        return arbitro;
    }
    
    public void setArbitro(Arbitro arbitro) {
        this.arbitro = arbitro;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    // Métodos de conveniencia
    public boolean isSuccess() {
        return status == Status.AUTENTICADO;
    }
    
    public boolean isPhoneNotFound() {
        return status == Status.TELEFONO_NO_ENCONTRADO;
    }
    
    public boolean isCedulaNotFound() {
        return status == Status.CEDULA_NO_ENCONTRADA;
    }
    
    public boolean isError() {
        return status == Status.ERROR_VALIDACION || status == Status.ERROR_SISTEMA;
    }
    
    @Override
    public String toString() {
        return String.format("AuthResult{status=%s, arbitro=%s, mensaje='%s'}", 
                           status, 
                           arbitro != null ? arbitro.getNombre() : "null", 
                           mensaje);
    }
}
