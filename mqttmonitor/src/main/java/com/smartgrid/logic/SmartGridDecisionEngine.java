package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
import com.smartgrid.service.MedicionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Motor de decisiones que gestiona los dispositivos conectados
 * en función del consumo energético y su nivel de criticidad.
 */
public class SmartGridDecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(SmartGridDecisionEngine.class);

    private final double limiteConsumo = 5000.0;
    private final Map<String, Dispositivo> dispositivosActivos = new HashMap<>();
    private final MedicionService medicionService;

    private boolean alertaCriticos = false;

    public SmartGridDecisionEngine(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    public void procesarDispositivo(Dispositivo dispositivo) {
        dispositivosActivos.put(dispositivo.getNombre(), dispositivo);
        alertaCriticos = false;

        double consumoTotal = getConsumoTotal();

        if (consumoTotal <= limiteConsumo) {
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return;
        }

        log.warn("⚠️ Consumo excedido: {}W > {}W", consumoTotal, limiteConsumo);

        List<Dispositivo> criticos = new ArrayList<>();
        List<Dispositivo> noCriticos = new ArrayList<>();

        for (Dispositivo d : dispositivosActivos.values()) {
            if (d.getCriticidad() == NivelCriticidad.CRITICA) {
                criticos.add(d);
            } else {
                noCriticos.add(d);
            }
        }

        noCriticos.sort(Comparator.comparingDouble(Dispositivo::getConsumo).reversed());

        double consumoCriticos = criticos.stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();

        if (consumoCriticos > limiteConsumo) {
            alertaCriticos = true;
            log.error("🚨 El consumo de dispositivos críticos ({}W) supera el límite ({}W)", consumoCriticos, limiteConsumo);
            log.error("🛑 No se pueden desconectar dispositivos críticos automáticamente.");
            log.error("🔔 Se requiere intervención manual.");
            return;
        }

        double consumoActual = consumoTotal;
        List<String> desconectados = new ArrayList<>();

        for (Dispositivo d : noCriticos) {
            if (consumoActual <= limiteConsumo) break;
            consumoActual -= d.getConsumo();
            desconectarYRegistrar(d);
            desconectados.add(d.getNombre());
        }

        if (!desconectados.isEmpty()) {
            log.info("🔌 Dispositivos no críticos desconectados: {}", String.join(", ", desconectados));
            log.info("⚡ Consumo tras desconexión: {}W / {}W", consumoActual, limiteConsumo);
        }

        if (consumoActual > limiteConsumo) {
            alertaCriticos = true;
            log.error("⚠️ Consumo aún elevado después de desconectar todos los no críticos: {}W", consumoActual);
            log.error("🔔 Se requiere intervención manual.");
        }
    }

    public void desconectarDispositivo(String nombre) {
        Dispositivo dispositivo = dispositivosActivos.get(nombre);
        if (dispositivo != null) {
            desconectarYRegistrar(dispositivo);
        }
    }

    public boolean ajustarPotenciaDispositivo(String nombre, double nuevaPotencia) {
        Dispositivo dispositivo = dispositivosActivos.get(nombre);

        if (dispositivo == null || dispositivo.getCriticidad() != NivelCriticidad.CRITICA) {
            return false;
        }

        double potenciaAnterior = dispositivo.getConsumo();
        dispositivo.setConsumo(nuevaPotencia);

        double consumoTotal = getConsumoTotal();

        if (consumoTotal <= limiteConsumo) {
            alertaCriticos = false;
            medicionService.registrar(dispositivo.getNombre(), nuevaPotencia);
            log.info("🔧 Potencia ajustada para '{}' a {}W", nombre, nuevaPotencia);
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return true;
        }

        dispositivo.setConsumo(potenciaAnterior);
        log.warn("⚠️ No se pudo ajustar potencia de '{}' a {}W. Excede el límite.", nombre, nuevaPotencia);
        return false;
    }

    public List<Dispositivo> getDispositivosActivos() {
        return new ArrayList<>(dispositivosActivos.values());
    }

    public double getConsumoTotal() {
        return dispositivosActivos.values().stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();
    }

    public boolean isAlertaCriticos() {
        return alertaCriticos;
    }

    public double getLimiteConsumo() {
        return limiteConsumo;
    }

    private void desconectarYRegistrar(Dispositivo dispositivo) {
        dispositivosActivos.remove(dispositivo.getNombre());
        log.info("🛑 Dispositivo '{}' desconectado", dispositivo.getNombre());

        if (medicionService != null) {
            medicionService.registrar(dispositivo.getNombre(), 0.0);
        }
    }

    /**
     * Aplica una oscilación de consumo negativa temporal a todos los dispositivos activos.
     * Después de 3 segundos, los valores originales se restauran automáticamente.
     *
     * @param variacion Cantidad en watts a restar temporalmente del consumo
     */
    public void aplicarOscilacionTemporal(double variacion) {
        if (variacion <= 0) return;

        Map<String, Double> consumoOriginal = new HashMap<>();
        log.warn("⚡ Aplicando oscilación temporal de -{}W a todos los dispositivos", variacion);

        for (Dispositivo d : dispositivosActivos.values()) {
            double original = d.getConsumo();
            consumoOriginal.put(d.getNombre(), original);

            double ajustado = Math.max(0.0, original - variacion);
            d.setConsumo(ajustado);
            medicionService.registrar(d.getNombre(), ajustado);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Dispositivo d : dispositivosActivos.values()) {
                    Double original = consumoOriginal.get(d.getNombre());
                    if (original != null) {
                        d.setConsumo(original);
                        medicionService.registrar(d.getNombre(), original);
                    }
                }
                log.info("🔄 Oscilación finalizada. Valores restaurados tras 3 segundos.");
            }
        }, 3000); // 3 segundos
    }
}
