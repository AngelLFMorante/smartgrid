package com.smartgrid.service;

import com.smartgrid.config.MQTTConfig;
import com.smartgrid.repository.DispositivoRepository;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import com.smartgrid.logic.SmartGridDecisionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que se suscribe a un topic MQTT para recibir información de sensores.
 */
@Service
public class MQTTSubscriberService {

    private static final Logger log = LoggerFactory.getLogger(MQTTSubscriberService.class);

    private final MQTTConfig mqttConfig; // Inyectado mediante el constructor

    private MqttClient client;

    // Motor de decisiones (IA simulada)
    private final SmartGridDecisionEngine ia;

    private final DispositivoRepository dispositivoRepository;

    private boolean alreadyInitialized = false;

    @Autowired
    public MQTTSubscriberService(MQTTConfig mqttConfig, SmartGridDecisionEngine ia, DispositivoRepository dispositivoRepository) {
        this.mqttConfig = mqttConfig;
        this.ia = ia;
        this.dispositivoRepository = dispositivoRepository;
    }

    @PostConstruct
    public void init() {
        if (alreadyInitialized) return;
        alreadyInitialized = true;

        try {
            String brokerUrl = mqttConfig.getBrokerUrl();  // Configuración del broker
            String clientId = mqttConfig.getClientId();    // Configuración del cliente
            String topic = mqttConfig.getTopic();

            client = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);  // Inicia una sesión limpia
            options.setKeepAliveInterval(30); // Mantiene la conexión viva por 30 segundos

            // Intentamos conectar
            client.connect(options);
            client.subscribe(topic, (topicSus, msg) -> {
                procesarMensaje(new String(msg.getPayload()));
            });

            log.info("✅ Suscrito a MQTT broker en '{}', topic '{}'", brokerUrl, topic);
            log.info("✅ Conexión exitosa al broker MQTT '{}'", brokerUrl);

            // Aquí no hacemos ninguna suscripción ni callback, solo mantenemos la conexión abierta

        } catch (MqttException e) {
            log.error("❌ Error al conectar con MQTT Broker: {}", e.getMessage(), e);
        }
    }

    public void procesarMensaje(String payload) {
        String[] partes = payload.split(":");
        if (partes.length == 2) {
            String nombre = partes[0].trim().toLowerCase();
            double consumo = Double.parseDouble(partes[1]);

            dispositivoRepository.findByNombre(nombre).ifPresentOrElse(dispositivo -> {
                dispositivo.setConsumo(consumo);
                ia.procesarDispositivo(dispositivo);
            }, () -> log.warn("❌ Dispositivo desconocido '{}'. Debe ser registrado antes de usar.", nombre));
        } else {
            log.warn("❌ Formato de mensaje inválido: '{}'", payload);
        }
    }
}
