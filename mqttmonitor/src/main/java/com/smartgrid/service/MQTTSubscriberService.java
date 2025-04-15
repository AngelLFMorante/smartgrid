package com.smartgrid.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

public class MQTTSubscriberService {

    private static final Logger log = LoggerFactory.getLogger(MQTTSubscriberService.class);
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883"; // puedes cambiar por uno privado
    private static final String CLIENT_ID = "java-smartgrid-subscriber";
    private static final String TOPIC = "smartgrid/consumption";

    private MqttClient client;

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);

            client.subscribe(TOPIC, (topic, msg) -> {
                String payload = new String(msg.getPayload());
                log.info("⚡ Mensaje recibido: {}", payload);

                double consumo = Double.parseDouble(payload);
                if (consumo > 3000) {
                    log.warn("⚠️ Exceso de consumo detectado: {}W - Simulando apagado automático", consumo);
                    // Aquí puedes simular lógica para apagar el dispositivo
                }
            });

            log.info("✅ Suscrito a MQTT broker en '{}', topic '{}'", BROKER_URL, TOPIC);

        } catch (MqttException e) {
            log.error("❌ Error al conectar con MQTT Broker: {}", e.getMessage(), e);
        }
    }
}
