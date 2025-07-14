package com.saga.controller;

import com.saga.model.Arbitro;
import com.saga.service.ArbitroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    @GetMapping("/arbitros")
    public List<Arbitro> obtenerArbitros() {
        return arbitroService.obtenerArbitrosDesdeExcel();
    }
}
