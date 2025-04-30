package com.smartgrid.service;

import com.smartgrid.model.Incidencia;
import com.smartgrid.repository.IncidenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenciaService {

    private final IncidenciaRepository repo;

    public IncidenciaService(IncidenciaRepository repo) {
        this.repo = repo;
    }

    public void registrar(String descripcion, String severidad) {
        repo.save(new Incidencia(descripcion, severidad));
    }

    public List<Incidencia> obtenerTodas() {
        return repo.findAll();
    }
}
