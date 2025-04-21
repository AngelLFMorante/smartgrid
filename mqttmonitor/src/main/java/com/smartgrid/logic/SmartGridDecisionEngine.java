package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Motor de decisiones de la Smart Grid.
 * Simula la lógica de gestión del consumo energético:
 * si el consumo total supera un umbral, apaga el dispositivo menos crítico y más consumidor.
 */
public class SmartGridDecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(SmartGridDecisionEngine.class);

    /** Umbral máximo de consumo energético (en vatios) permitido por el sistema. */
    private static final double CONSUMO_MAXIMO_PERMITIDO = 5000.0;

    /** Mapa que almacena los dispositivos activos por nombre. */
    private final Map<String, Dispositivo> dispositivos = new HashMap<>();

    /**
     * Procesa un dispositivo recibido desde MQTT, actualiza su consumo,
     * evalúa el total y aplica lógica de desconexión si se excede el límite.
     *
     * @param dispositivo el dispositivo a procesar
     */
    public void procesarDispositivo(Dispositivo dispositivo) {
        dispositivos.put(dispositivo.getNombre(), dispositivo);

        double total = dispositivos.values().stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();

        log.info("🔍 Consumo total actual: {}W", total);

        if (total > CONSUMO_MAXIMO_PERMITIDO) {
            dispositivos.values().stream()
                    .filter(d -> d.getCriticidad() != Dispositivo.Criticidad.CRITICA)
                    .sorted((a, b) -> Double.compare(b.getConsumo(), a.getConsumo()))
                    .findFirst()
                    .ifPresent(d -> {
                        log.warn("⚠️ Superado umbral. Apagando '{}'", d.getNombre());
                        dispositivos.remove(d.getNombre());
                    });
        }
    }

    /**
     * Devuelve los dispositivos activos actualmente en memoria.
     *
     * @return mapa de dispositivos por nombre
     */
    public Map<String, Dispositivo> getDispositivosActivos() {
        return dispositivos;
    }
}
