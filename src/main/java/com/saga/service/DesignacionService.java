package com.saga.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.dto.DesignacionRequest;
import com.saga.model.Arbitro;
import com.saga.model.Designacion;
import com.saga.model.Partido;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DesignacionService {

    private final List<Designacion> designaciones = new ArrayList<>();
    private final PartidoService partidoService;
    private final ArbitroService arbitroService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final File archivo;

    public DesignacionService(PartidoService partidoService, ArbitroService arbitroService) {
        this.partidoService = partidoService;
        this.arbitroService = arbitroService;

        // Ruta al archivo JSON de designaciones
        URL resource = getClass().getClassLoader().getResource("data/designaciones.json");
        this.archivo = resource != null ? new File(resource.getFile()) : new File("src/main/resources/data/designaciones.json");

        cargarDesdeArchivo();
    }

    private void cargarDesdeArchivo() {
        try {
            if (archivo.exists()) {
                List<Designacion> lista = mapper.readValue(archivo, new TypeReference<List<Designacion>>() {});
                designaciones.addAll(lista);
                System.out.println("✅ Designaciones cargadas desde archivo");
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando designaciones: " + e.getMessage());
        }
    }

    private void guardarEnArchivo() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, designaciones);
            System.out.println("💾 Designaciones guardadas");
        } catch (Exception e) {
            System.err.println("❌ Error guardando designaciones: " + e.getMessage());
        }
    }

    public String asignarArbitro(DesignacionRequest request) {
        Partido partido = partidoService.buscarPartidoPorId(request.getPartidoId());
        Arbitro arbitro = arbitroService.buscarArbitroPorNombre(request.getArbitroNombre());

        if (partido == null || arbitro == null) {
            return "Partido o Árbitro no encontrado";
        }

        Designacion designacion = new Designacion(arbitro, partido, request.getRol());
        designaciones.add(designacion);
        guardarEnArchivo();

        return "Asignación registrada con éxito";
    }

    public List<Designacion> getDesignaciones() {
        return designaciones;
    }
}
