package com.saga.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saga.model.Partido;
import com.saga.telegram.service.TelegramNotificationService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    @Autowired
    private TelegramNotificationService notificationService;
    
    /**
     * Endpoint para notificar asignación de partido a un árbitro
     */
    @PostMapping("/asignar/{cedula}")
    public ResponseEntity<String> notificarAsignacion(
            @PathVariable String cedula, 
            @RequestBody Partido partido) {
        
        boolean enviado = notificationService.notificarAsignacionPartido(cedula, partido);
        
        if (enviado) {
            return ResponseEntity.ok("Notificación enviada exitosamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo enviar la notificación. Verifica que el árbitro tenga Telegram vinculado.");
        }
    }
    
    /**
     * Endpoint para notificar asignación múltiple
     */
    @PostMapping("/asignar-multiple")
    public ResponseEntity<String> notificarAsignacionMultiple(@RequestBody AsignacionMultipleRequest request) {
        notificationService.notificarAsignacionMultiple(request.getCedulasArbitros(), request.getPartido());
        return ResponseEntity.ok("Notificaciones enviadas");
    }
    
    /**
     * Endpoint para enviar recordatorio de partido
     */
    @PostMapping("/recordatorio/{cedula}/{horas}")
    public ResponseEntity<String> enviarRecordatorio(
            @PathVariable String cedula,
            @PathVariable int horas,
            @RequestBody Partido partido) {
        
        boolean enviado = notificationService.enviarRecordatorioPartido(cedula, partido, horas);
        
        if (enviado) {
            return ResponseEntity.ok("Recordatorio enviado exitosamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo enviar el recordatorio");
        }
    }
    
    /**
     * Endpoint para verificar árbitros con Telegram
     */
    @GetMapping("/verificar-telegram")
    public ResponseEntity<String> verificarArbitrosConTelegram() {
        notificationService.verificarArbitrosConTelegram();
        return ResponseEntity.ok("Verificación completada. Revisa los logs para ver los resultados.");
    }
    
    /**
     * Clase para request de asignación múltiple
     */
    public static class AsignacionMultipleRequest {
        private List<String> cedulasArbitros;
        private Partido partido;
        
        public List<String> getCedulasArbitros() {
            return cedulasArbitros;
        }
        
        public void setCedulasArbitros(List<String> cedulasArbitros) {
            this.cedulasArbitros = cedulasArbitros;
        }
        
        public Partido getPartido() {
            return partido;
        }
        
        public void setPartido(Partido partido) {
            this.partido = partido;
        }
    }
}
