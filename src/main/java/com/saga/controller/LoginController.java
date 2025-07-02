package com.saga.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Para permitir peticiones desde el frontend local
public class LoginController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> respuesta = new HashMap<>();

        // Validación básica (esto luego se conectará con tu lógica real)
        if ("admin".equals(request.getUsuario()) && "1234".equals(request.getContrasena())) {
            respuesta.put("estado", "exito");
            respuesta.put("mensaje", "Inicio de sesión exitoso");
        } else {
            respuesta.put("estado", "error");
            respuesta.put("mensaje", "Usuario o contraseña incorrectos");
        }

        return respuesta;
    }
}
