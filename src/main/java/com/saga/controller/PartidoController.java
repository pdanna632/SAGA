package com.saga.controller;

import com.saga.model.Partido;
import com.saga.service.PartidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @GetMapping("/partidos")
    public List<Partido> obtenerPartidos() {
        return partidoService.obtenerPartidosDesdeExcel();
    }
}
