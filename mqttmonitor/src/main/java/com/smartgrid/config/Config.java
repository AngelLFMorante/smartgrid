package com.smartgrid.config;

import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.repository.DispositivoRepository;
import com.smartgrid.service.MQTTSubscriberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración de Spring que define los beans necesarios para la aplicación,
 * en especial los relacionados con la comunicación MQTT y el motor de decisiones.
 */
@Configuration
public class Config {

    /**
     * Bean que gestiona el servicio suscriptor de MQTT.
     * Este servicio escucha mensajes desde el broker MQTT y los procesa.
     *
     * @param mqttConfig configuración del cliente MQTT
     * @param ia motor de decisión de la smart grid
     * @param repository repositorio JPA para acceder a los dispositivos
     * @return instancia lista para usar de MQTTSubscriberService
     */
    @Bean
    public MQTTSubscriberService mqttSubscriberService(MQTTConfig mqttConfig, SmartGridDecisionEngine ia, DispositivoRepository repository) {
        return new MQTTSubscriberService(mqttConfig, ia, repository);
    }

    /**
     * Bean del motor de decisiones que simula la lógica de control energético.
     *
     * @return nueva instancia de SmartGridDecisionEngine
     */
    @Bean
    public SmartGridDecisionEngine decisionEngine() {
        return new SmartGridDecisionEngine();
    }
}
