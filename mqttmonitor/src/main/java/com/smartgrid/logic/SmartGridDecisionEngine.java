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
 *
 * Esta clase no está anotada con @Component y debe ser registrada manualmente
 * como un bean en la configuración de Spring para permitir la inyección de dependencias.
 */
public class SmartGridDecisionEngine {

    //TODO hay que controlar cuando se setean los datos al bajar el consumo
    // tambien hay que controlar las oscilaciones para que sean reales a parte que se quede informado
    // Hay que implementar forecast del link mas la IA para sacar los analisis segun balance de consumo registrado

    private static final Logger log = LoggerFactory.getLogger(SmartGridDecisionEngine.class);

    // Límite de consumo total permitido en Watts
    private final double limiteConsumo = 5000.0;

    // Dispositivos activos actualmente (nombre -> dispositivo)
    private final Map<String, Dispositivo> dispositivosActivos = new HashMap<>();

    // Servicio de registro de mediciones
    private final MedicionService medicionService;

    // Estado de alerta: true si hay solo dispositivos críticos activos y aún se supera el límite
    private boolean alertaCriticos = false;

    /**
     * Constructor principal del motor de decisiones.
     *
     * @param medicionService Servicio de mediciones para registrar el consumo de los dispositivos
     */
    public SmartGridDecisionEngine(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    /**
     * Procesa un nuevo dispositivo o actualización, y toma decisiones
     * sobre su conexión basada en el consumo total del sistema.
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

        // Separar dispositivos por criticidad
        List<Dispositivo> criticos = new ArrayList<>();
        List<Dispositivo> noCriticos = new ArrayList<>();

        for (Dispositivo d : dispositivosActivos.values()) {
            if (d.getCriticidad() == NivelCriticidad.CRITICA) {
                criticos.add(d);
            } else {
                noCriticos.add(d);
            }
        }

        // Ordenar no críticos por mayor consumo
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

        // Desconectar dispositivos no críticos hasta ajustar el consumo
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

    /**
     * Desconecta un dispositivo manualmente por nombre.
     * También registra una medición de consumo 0.
     *
     * @param nombre Nombre del dispositivo a desconectar
     */
    public void desconectarDispositivo(String nombre) {
        Dispositivo dispositivo = dispositivosActivos.get(nombre);
        if (dispositivo != null) {
            desconectarYRegistrar(dispositivo);
        }
    }

    /**
     * Ajusta la potencia de un dispositivo crítico, si es posible.
     *
     * @param nombre        Nombre del dispositivo
     * @param nuevaPotencia Nueva potencia a aplicar
     * @return true si se pudo ajustar, false si se revierte por sobrepasar el límite
     */
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
            log.info("🔧 Potencia ajustada para '{}' a {}W", nombre, nuevaPotencia);
            log.info("✅ Consumo dentro del límite: {}W / {}W", consumoTotal, limiteConsumo);
            return true;
        }

        // Revertir si excede el límite
        dispositivo.setConsumo(potenciaAnterior);
        log.warn("⚠️ No se pudo ajustar potencia de '{}' a {}W. Excede el límite.", nombre, nuevaPotencia);
        return false;
    }

    /**
     * Devuelve la lista de dispositivos actualmente activos.
     *
     * @return Lista de dispositivos activos
     */
    public List<Dispositivo> getDispositivosActivos() {
        return new ArrayList<>(dispositivosActivos.values());
    }

    /**
     * Devuelve el consumo total actual del sistema.
     *
     * @return Consumo total en Watts
     */
    public double getConsumoTotal() {
        return dispositivosActivos.values().stream()
                .mapToDouble(Dispositivo::getConsumo)
                .sum();
    }

    /**
     * Indica si hay una alerta activa por exceso de consumo con solo dispositivos críticos.
     *
     * @return true si hay alerta, false en caso contrario
     */
    public boolean isAlertaCriticos() {
        return alertaCriticos;
    }

    /**
     * Devuelve el límite de consumo configurado.
     *
     * @return Límite en Watts
     */
    public double getLimiteConsumo() {
        return limiteConsumo;
    }

    /**
     * Desconecta el dispositivo y registra una medición de 0W para su consumo.
     * Se asegura de que se registre solo una vez.
     *
     * @param dispositivo Dispositivo a desconectar
     */
    private void desconectarYRegistrar(Dispositivo dispositivo) {
        dispositivosActivos.remove(dispositivo.getNombre());
        log.info("🛑 Dispositivo '{}' desconectado", dispositivo.getNombre());

        if (medicionService != null) {
            medicionService.registrar(dispositivo.getNombre(), 0.0);
        }
    }
}
