package com.saga.service;

import com.saga.dto.DesignacionRequest;
import com.saga.model.Arbitro;
import com.saga.model.Designacion;
import com.saga.model.Partido;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DesignacionService {

    private final List<Designacion> designaciones = new ArrayList<>();

    private final PartidoService partidoService;
    private final ArbitroService arbitroService;

    public DesignacionService(PartidoService partidoService, ArbitroService arbitroService) {
        this.partidoService = partidoService;
        this.arbitroService = arbitroService;
    }

    public String asignarArbitro(DesignacionRequest request) {
        Partido partido = partidoService.buscarPartidoPorId(request.getPartidoId());
        Arbitro arbitro = arbitroService.buscarArbitroPorNombre(request.getArbitroNombre());

        if (partido == null || arbitro == null) {
            return "❌ Partido o Árbitro no encontrado";
        }

        // Verificar si ya existe la asignación
        for (Designacion d : designaciones) {
            if (d.getPartido().getId().equals(partido.getId()) &&
                d.getArbitro().getNombre().equalsIgnoreCase(arbitro.getNombre())) {
                return "⚠️ Este árbitro ya está asignado a este partido";
            }
        }

        Designacion designacion = new Designacion(arbitro, partido, request.getRol());
        designaciones.add(designacion);

        return "✅ Asignación registrada con éxito";
    }

    public List<Designacion> getDesignaciones() {
        return designaciones;
    }
}
