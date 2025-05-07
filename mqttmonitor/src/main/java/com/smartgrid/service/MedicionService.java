package com.smartgrid.service;

import com.smartgrid.model.Medicion;
import com.smartgrid.repository.MedicionRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class MedicionService {

    private final MedicionRepository repo;

    public MedicionService(MedicionRepository repo) {
        this.repo = repo;
    }

    public void registrar(String nombreDispositivo, double consumo) {
        repo.save(new Medicion(nombreDispositivo, consumo, LocalDateTime.now()));
    }

    public void eliminarAntiguas() {
        LocalDateTime limite = LocalDateTime.now().minusMonths(6);
        repo.deleteByFechaHoraBefore(limite);
    }
}

