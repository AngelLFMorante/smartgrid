package com.smartgrid.config;

import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.service.MQTTSubscriberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans relacionados con MQTT para inyección en Spring.
 */
@Configuration
public class MQTTConfig {

    /**
     * Define el bean del servicio suscriptor MQTT para que Spring lo gestione.
     *
     * @return una instancia de MQTTSubscriberService
     */
    @Bean
    public MQTTSubscriberService mqttSubscriberService(SmartGridDecisionEngine ia) {
        return new MQTTSubscriberService(ia);
    }

    /**
     *
     * @return una instancia de SmartGridDecisionEngine
     */
    @Bean
    public SmartGridDecisionEngine decisionEngine() {
        return new SmartGridDecisionEngine();
    }

}
