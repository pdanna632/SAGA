package com.saga.controller;

import com.saga.dto.DesignacionRequest;
import com.saga.model.Designacion;
import com.saga.service.DesignacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class DesignacionController {

    private final DesignacionService designacionService;

    public DesignacionController(DesignacionService designacionService) {
        this.designacionService = designacionService;
    }

    @PostMapping
    public String asignarArbitro(@RequestBody DesignacionRequest request) {
        return designacionService.asignarArbitro(request);
    }

    @GetMapping
    public List<Designacion> obtenerDesignaciones() {
        return designacionService.getDesignaciones();
    }
}
