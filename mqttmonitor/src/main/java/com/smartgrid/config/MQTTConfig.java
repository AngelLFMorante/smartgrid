package com.smartgrid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuración del cliente MQTT, leída desde el archivo de propiedades.
 * Contiene la URL del broker, el topic a suscribirse y el ID del cliente.
 */
@Component
public class MQTTConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.client.id}")
    private String clientId;

    /**
     * Obtiene la URL del broker MQTT.
     *
     * @return URL del broker
     */
    public String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Obtiene el topic al que se suscribe el cliente.
     *
     * @return nombre del topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Obtiene el identificador único del cliente MQTT.
     *
     * @return ID del cliente
     */
    public String getClientId() {
        return clientId;
    }
}
