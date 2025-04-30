package com.smartgrid.logic;

import com.smartgrid.dto.DispositivoDTO;
import com.smartgrid.model.Dispositivo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que actúa como intermediario entre el motor de decisiones
 * y el controlador, proporcionando DTOs adecuados para la vista.
 */
@Service
public class SmartGridService {

    private final SmartGridDecisionEngine engine;

    public SmartGridService(SmartGridDecisionEngine engine) {
        this.engine = engine;
    }

    /**
     * Devuelve los dispositivos activos como DTOs.
     */
    public List<DispositivoDTO> obtenerDispositivosActivos() {
        return engine.getDispositivosActivos().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve el consumo total actual.
     */
    public double obtenerConsumoTotal() {
        return engine.getConsumoTotal();
    }

    public double getLimiteConsumo() {
        return engine.getLimiteConsumo();
    }

    public boolean hayAlertaCriticos() {
        return engine.isAlertaCriticos();
    }

    public void desconectar(String nombre) {
        engine.desconectarDispositivo(nombre);
    }

    public boolean ajustarPotencia(String nombre, double nuevaPotencia) {
        return engine.ajustarPotenciaDispositivo(nombre, nuevaPotencia);
    }

    private DispositivoDTO toDto(Dispositivo dispositivo) {
        return new DispositivoDTO(
                dispositivo.getNombre(),
                dispositivo.getZona(),
                dispositivo.getCriticidad().name(),
                dispositivo.getConsumo()
        );
    }
}
