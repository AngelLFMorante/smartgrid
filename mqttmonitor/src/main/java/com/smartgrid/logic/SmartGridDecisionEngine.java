package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulación de motor de decisiones basado en consumo energético.
 * Si el consumo total excede un umbral, apaga el dispositivo que más consume.
 */
public class SmartGridDecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(SmartGridDecisionEngine.class);

    /** Límite máximo de consumo permitido (Watts). */
    private static final double CONSUMO_MAXIMO_PERMITIDO = 5000.0;

    /** Almacena el consumo actual de cada dispositivo. */
    private final Map<String, Dispositivo> dispositivos = new HashMap<>();

    public void procesarDispositivo(Dispositivo dispositivo) {
        dispositivos.put(dispositivo.getNombre(), dispositivo);

        double total = dispositivos.values().stream().mapToDouble(Dispositivo::getConsumo).sum();
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
     * Procesa el consumo de un dispositivo.
     * Si el total supera el límite, apaga el que más consume.
     *
     * @param dispositivo nombre del dispositivo
     * @param consumoWatts cantidad de consumo reportado en watts
     */
    /*public void procesarConsumo(String dispositivo, double consumoWatts) {
        dispositivos.put(dispositivo, consumoWatts);

        double total = dispositivos.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        log.info("🔍 Consumo total actual: {}W", total);

        // Verificamos si se supera el límite permitido
        if (total > CONSUMO_MAXIMO_PERMITIDO) {
            // Seleccionamos el dispositivo que más consume
            String aApagar = dispositivos.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (aApagar != null) {
                log.warn("⚠️ Superado el umbral. Apagando '{}'", aApagar);
                dispositivos.remove(aApagar); // Simulamos el apagado
            }
        }
    }*/

    /**
     * Devuelve los dispositivos actualmente activos.
     *
     * @return mapa de dispositivos y su consumo
     */
    public Map<String, Dispositivo> getDispositivosActivos() {
        return dispositivos;
    }
}
