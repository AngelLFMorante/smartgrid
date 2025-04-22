package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Motor de decisiones que gestiona los dispositivos conectados
 * en función del consumo energético y su criticidad.
 */
public class SmartGridDecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(SmartGridDecisionEngine.class);

    // Límite de consumo total permitido en Watts
    private final double limiteConsumo = 5000.0;

    // Dispositivos activos actualmente (nombre -> dispositivo)
    private final Map<String, Dispositivo> dispositivosActivos = new HashMap<>();

    // Flag que indica si hay una situación de alerta por exceso de consumo solo con dispositivos críticos
    private boolean alertaCriticos = false;

    /**
     * Procesa el dispositivo recibido y determina si puede mantenerse activo.
     * Si el consumo total supera el límite, se intentan desconectar dispositivos no críticos.
     * Si no es posible reducir el consumo, se activa una alerta.
     *
     * @param dispositivo Dispositivo con datos actualizados
     */
    public void procesarDispositivo(Dispositivo dispositivo) {
        dispositivosActivos.put(dispositivo.getNombre(), dispositivo);
        alertaCriticos = false; // Reiniciar estado de alerta

        double consumoTotal = getConsumoTotal();

        if (consumoTotal <= limiteConsumo) {
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return;
        }

        log.warn("⚠️ Consumo excedido: {}W > {}W", consumoTotal, limiteConsumo);

        List<Dispositivo> criticos = dispositivosActivos.values().stream()
                .filter(d -> d.getCriticidad() == NivelCriticidad.CRITICA)
                .toList();

        List<Dispositivo> noCriticos = dispositivosActivos.values().stream()
                .filter(d -> d.getCriticidad() != NivelCriticidad.CRITICA)
                .sorted(Comparator.comparingDouble(Dispositivo::getConsumo).reversed())
                .toList();

        double consumoCriticos = criticos.stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();

        if (consumoCriticos > limiteConsumo) {
            alertaCriticos = true;
            log.error("🚨 Consumo solo de dispositivos críticos ({:.0f}W) supera el límite ({:.0f}W)",
                    consumoCriticos, limiteConsumo);
            log.error("🛑 No se pueden desconectar dispositivos críticos automáticamente.");
            log.error("🔔 Intervención manual requerida para gestionar dispositivos críticos.");
            return;
        }

        double consumoActual = consumoTotal;
        List<String> desconectados = new ArrayList<>();

        for (Dispositivo d : noCriticos) {
            if (consumoActual <= limiteConsumo) break;

            consumoActual -= d.getConsumo();
            dispositivosActivos.remove(d.getNombre());
            desconectados.add(d.getNombre());
        }

        if (!desconectados.isEmpty()) {
            log.info("🔌 Dispositivos no críticos desconectados: {}", String.join(", ", desconectados));
            log.info("⚡ Consumo tras desconexión: {:.0f}W / {:.0f}W", consumoActual, limiteConsumo);
        }

        if (consumoActual > limiteConsumo) {
            alertaCriticos = true;
            log.error("⚠️ Consumo aún elevado después de desconectar todos los no críticos: {:.0f}W", consumoActual);
            log.error("🔔 Intervención manual requerida para gestionar dispositivos críticos.");
        }
    }

    /**
     * Devuelve un mapa de los dispositivos activos con su consumo actual.
     */
    public List<Dispositivo> getDispositivosActivos() {
        return new ArrayList<>(dispositivosActivos.values());
    }

    /**
     * Devuelve el consumo total actual de todos los dispositivos activos.
     */
    public double getConsumoTotal() {
        return dispositivosActivos.values().stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();
    }

    /**
     * Elimina un dispositivo del mapa de activos, usado para gestión manual.
     *
     * @param nombre Nombre del dispositivo a desconectar
     */
    public void desconectarDispositivo(String nombre) {
        dispositivosActivos.remove(nombre);
        log.info("🛑 Dispositivo '{}' desconectado manualmente", nombre);
    }

    /**
     * Indica si se ha generado una alerta por consumo excesivo
     * con solo dispositivos críticos activos.
     *
     * @return true si hay alerta crítica, false en caso contrario
     */
    public boolean isAlertaCriticos() {
        return alertaCriticos;
    }

}
