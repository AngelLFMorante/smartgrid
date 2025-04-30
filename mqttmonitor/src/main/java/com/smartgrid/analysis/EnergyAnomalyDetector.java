package com.smartgrid.analysis;

import com.smartgrid.service.IncidenciaService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Componente encargado de analizar valores energéticos recibidos desde sensores
 * para detectar oscilaciones o comportamientos anómalos en el voltaje.
 * Registra internamente las últimas oscilaciones y, en caso de anomalía,
 * persiste una incidencia para su visualización en el historial.
 */
@Component
public class EnergyAnomalyDetector {

    private static final double VOLTAJE_REFERENCIA = 220.0;
    private static final double DESVIACION_PERMITIDA = 10.0;

    private final IncidenciaService incidenciaService;

    // Lista circular simple en memoria para guardar las últimas oscilaciones detectadas
    private final List<String> oscilacionesRecientes = new LinkedList<>();
    private static final int MAX_OSCILACIONES = 10;

    public EnergyAnomalyDetector(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    /**
     * Verifica si el voltaje actual se desvía de la referencia permitida y,
     * en tal caso, lo considera una oscilación anómala.
     *
     * @param voltajeActual Voltaje medido actualmente
     * @return true si el voltaje representa una oscilación anómala
     */
    public boolean esOscilacionAnomala(double voltajeActual) {
        boolean anomala = voltajeActual < (VOLTAJE_REFERENCIA - DESVIACION_PERMITIDA)
                || voltajeActual > (VOLTAJE_REFERENCIA + DESVIACION_PERMITIDA);

        if (anomala) {
            registrarOscilacion(voltajeActual);
        }

        return anomala;
    }

    /**
     * Registra internamente la oscilación en memoria (para mostrar en dashboard)
     * y también la guarda como incidencia persistente en el sistema.
     *
     * @param voltaje Voltaje detectado que constituye una oscilación
     */
    private void registrarOscilacion(double voltaje) {
        String timestamp = LocalDateTime.now().withNano(0).toString();
        String mensaje = String.format("⚠️ Oscilación detectada: %.1fV a las %s", voltaje, timestamp);

        synchronized (oscilacionesRecientes) {
            if (oscilacionesRecientes.size() >= MAX_OSCILACIONES) {
                oscilacionesRecientes.remove(0);
            }
            oscilacionesRecientes.add(mensaje);
        }

        // Registrar la incidencia en la base de datos para historial
        String descripcion = String.format("Oscilación de voltaje detectada: %.1fV", voltaje);
        incidenciaService.registrar(descripcion, "MEDIA");
    }

    /**
     * Devuelve una copia de las oscilaciones recientes registradas en memoria.
     *
     * @return Lista con mensajes de oscilaciones recientes
     */
    public List<String> getOscilacionesRecientes() {
        synchronized (oscilacionesRecientes) {
            return new LinkedList<>(oscilacionesRecientes);
        }
    }
}
