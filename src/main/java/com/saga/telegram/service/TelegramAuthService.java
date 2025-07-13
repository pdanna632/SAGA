package com.saga.telegram.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.saga.model.Arbitro;
import com.saga.utils.ExcelArbitroReader;

@Service
public class TelegramAuthService {
    
    private static final Logger logger = Logger.getLogger(TelegramAuthService.class.getName());
    
    // Cache de usuarios autenticados
    private final Map<String, Arbitro> usuariosAutenticados = new ConcurrentHashMap<>();
    
    // Estados de usuarios en proceso de verificación
    private final Map<String, String> usuariosEnVerificacion = new ConcurrentHashMap<>();
    
    /**
     * Verifica si un usuario ya está autenticado
     */
    public boolean isUsuarioAutenticado(String chatId) {
        return usuariosAutenticados.containsKey(chatId);
    }
    
    /**
     * Obtiene el árbitro autenticado por chat ID
     */
    public Arbitro getArbitroAutenticado(String chatId) {
        return usuariosAutenticados.get(chatId);
    }
    
    /**
     * Intenta autenticar un usuario por su número de teléfono
     * @param chatId Chat ID del usuario
     * @param phoneNumber Número de teléfono (si está disponible)
     * @return Resultado de la autenticación
     */
    public AuthResult autenticarPorTelefono(String chatId, String phoneNumber) {
        try {
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros("src/main/resources/data/Arbitros.xlsx");
            
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                // Normalizar número (remover espacios, caracteres especiales)
                String telefonoNormalizado = normalizarTelefono(phoneNumber);
                
                for (Arbitro arbitro : arbitros) {
                    String telefonoArbitro = normalizarTelefono(arbitro.getTelefono());
                    
                    if (telefonoNormalizado.equals(telefonoArbitro) && arbitro.isActivo()) {
                        // Árbitro encontrado y activo
                        arbitro.setChatId(chatId);
                        usuariosAutenticados.put(chatId, arbitro);
                        logger.info("Usuario autenticado exitosamente: " + arbitro.getNombre());
                        
                        return new AuthResult(AuthResult.Status.AUTENTICADO, arbitro, null);
                    }
                }
                
                // Número no encontrado
                return new AuthResult(AuthResult.Status.NUMERO_NO_ENCONTRADO, null, phoneNumber);
            }
            
            // Número no disponible
            return new AuthResult(AuthResult.Status.NUMERO_NO_DISPONIBLE, null, null);
            
        } catch (Exception e) {
            logger.severe("Error durante autenticación: " + e.getMessage());
            return new AuthResult(AuthResult.Status.ERROR, null, null);
        }
    }
    
    /**
     * Inicia el proceso de verificación por cédula
     */
    public void iniciarVerificacionPorCedula(String chatId) {
        usuariosEnVerificacion.put(chatId, "esperando_cedula");
    }
    
    /**
     * Verifica la cédula proporcionada
     */
    public AuthResult verificarPorCedula(String chatId, String cedula) {
        try {
            List<Arbitro> arbitros = ExcelArbitroReader.leerArbitros("src/main/resources/data/Arbitros.xlsx");
            
            for (Arbitro arbitro : arbitros) {
                if (arbitro.getCedula().equals(cedula.trim()) && arbitro.isActivo()) {
                    // Árbitro encontrado por cédula
                    arbitro.setChatId(chatId);
                    usuariosAutenticados.put(chatId, arbitro);
                    usuariosEnVerificacion.remove(chatId);
                    logger.info("Usuario verificado por cédula: " + arbitro.getNombre());
                    
                    return new AuthResult(AuthResult.Status.AUTENTICADO, arbitro, null);
                }
            }
            
            // Cédula no encontrada
            usuariosEnVerificacion.remove(chatId);
            return new AuthResult(AuthResult.Status.CEDULA_NO_ENCONTRADA, null, cedula);
            
        } catch (Exception e) {
            logger.severe("Error durante verificación por cédula: " + e.getMessage());
            usuariosEnVerificacion.remove(chatId);
            return new AuthResult(AuthResult.Status.ERROR, null, null);
        }
    }
    
    /**
     * Verifica si un usuario está esperando ingresar cédula
     */
    public boolean isEsperandoCedula(String chatId) {
        return "esperando_cedula".equals(usuariosEnVerificacion.get(chatId));
    }
    
    /**
     * Normaliza un número de teléfono para comparación
     */
    private String normalizarTelefono(String telefono) {
        if (telefono == null) return "";
        
        // Remover espacios, guiones, paréntesis y otros caracteres
        String normalizado = telefono.replaceAll("[\\s\\-\\(\\)\\+]", "");
        
        // Si empieza con código de país de Colombia (+57), removerlo
        if (normalizado.startsWith("57") && normalizado.length() > 10) {
            normalizado = normalizado.substring(2);
        }
        
        return normalizado;
    }
    
    /**
     * Cierra sesión de un usuario
     */
    public void cerrarSesion(String chatId) {
        usuariosAutenticados.remove(chatId);
        usuariosEnVerificacion.remove(chatId);
    }
    
    /**
     * Clase para representar el resultado de autenticación
     */
    public static class AuthResult {
        public enum Status {
            AUTENTICADO,
            NUMERO_NO_DISPONIBLE,
            NUMERO_NO_ENCONTRADO,
            CEDULA_NO_ENCONTRADA,
            ERROR
        }
        
        private final Status status;
        private final Arbitro arbitro;
        private final String numeroTelefono;
        
        public AuthResult(Status status, Arbitro arbitro, String numeroTelefono) {
            this.status = status;
            this.arbitro = arbitro;
            this.numeroTelefono = numeroTelefono;
        }
        
        public Status getStatus() { return status; }
        public Arbitro getArbitro() { return arbitro; }
        public String getNumeroTelefono() { return numeroTelefono; }
    }
}
