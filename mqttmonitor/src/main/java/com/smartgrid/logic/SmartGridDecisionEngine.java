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

        // Si el consumo total está por debajo del límite, desactivamos la alerta.
        if (consumoTotal <= limiteConsumo) {
            alertaCriticos = false; // Restablecemos la alerta cuando estamos dentro del límite
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return;
        }

        log.warn("⚠️ Consumo excedido: {}W > {}W", consumoTotal, limiteConsumo);

        // Filtrar dispositivos críticos
        List<Dispositivo> criticos = dispositivosActivos.values().stream()
                .filter(d -> d.getCriticidad() == NivelCriticidad.CRITICA)
                .toList();

        // Filtrar dispositivos no críticos
        List<Dispositivo> noCriticos = dispositivosActivos.values().stream()
                .filter(d -> d.getCriticidad() != NivelCriticidad.CRITICA)
                .sorted(Comparator.comparingDouble(Dispositivo::getConsumo).reversed())
                .toList();

        // Calcular el consumo solo de dispositivos críticos
        double consumoCriticos = criticos.stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();

        // Si el consumo de dispositivos críticos excede el límite, activamos la alerta y no desconectamos nada
        if (consumoCriticos > limiteConsumo) {
            alertaCriticos = true;
            log.error("🚨 Consumo solo de dispositivos críticos ({}W) supera el límite ({}W)",
                    consumoCriticos, limiteConsumo);
            log.error("🛑 No se pueden desconectar dispositivos críticos automáticamente.");
            log.error("🔔 Intervención manual requerida para gestionar dispositivos críticos.");
            return;
        }

        // Procedemos a desconectar dispositivos no críticos si el consumo total excede el límite
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

        // Si aún no se ha reducido el consumo al límite, activamos la alerta crítica ( dispositivos unicos criticos y se sigue conectando mas)
        if (consumoActual > limiteConsumo) {
            alertaCriticos = true;
            log.error("⚠️ Consumo aún elevado después de desconectar todos los no críticos: {:.0f}W", consumoActual);
            log.error("🔔 Intervención manual requerida para gestionar dispositivos críticos.");
        }
    }

    /**
     * Ajusta la potencia de un dispositivo crítico, de ser posible.
     *
     * @param nombre         nombre del dispositivo a ajustar
     * @param nuevaPotencia nueva potencia para el dispositivo
     * @return true si se pudo ajustar correctamente, false en caso contrario
     */
    public boolean ajustarPotenciaDispositivo(String nombre, double nuevaPotencia) {
        Dispositivo dispositivo = dispositivosActivos.get(nombre);

        if (dispositivo == null || dispositivo.getCriticidad() != NivelCriticidad.CRITICA) {
            return false; // No se puede ajustar potencia si no es crítico
        }

        // Ajustar la potencia
        dispositivo.setConsumo(nuevaPotencia);

        // Comprobar si el nuevo consumo cumple con el límite
        double consumoTotal = getConsumoTotal(); //TODO aqui hay que revisar bien si se ajustan los valores porque si esta ajustado debemos entrar en el if y no entra
        if (consumoTotal <= limiteConsumo) {
            alertaCriticos = false;
            log.info("🔧 Potencia ajustada para el dispositivo '{}' a {}W", nombre, nuevaPotencia);
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return true;
        }

        // Si el consumo total sigue siendo demasiado alto, revertir el ajuste
        dispositivo.setConsumo(dispositivo.getConsumo()); // revertir el consumo original
        //TODO hay que controlar bien los cambios de ajuste de potencia, porque sale como que no se puede ajustar porque excede ( si se ajusta pero sigue excediendo seguramente )
        log.warn("⚠️ No se pudo ajustar la potencia de '{}' a {}W debido a que excede el límite.", nombre, nuevaPotencia);
        return false;
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

    public double getLimiteConsumo() {
        return limiteConsumo;
    }
}
