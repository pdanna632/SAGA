package com.saga.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saga.model.Partido;
import com.saga.service.PartidoService;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {
    
    @Autowired
    private PartidoService partidoService;
    
    @GetMapping
    public List<Partido> obtenerTodosLosPartidos() {
        return partidoService.obtenerTodosLosPartidos();
    }
}
