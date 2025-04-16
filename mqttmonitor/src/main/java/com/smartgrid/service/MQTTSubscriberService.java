package com.smartgrid.service;

import com.smartgrid.repository.DispositivoRepository;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import com.smartgrid.logic.SmartGridDecisionEngine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servicio que se suscribe a un topic MQTT para recibir información de sensores.
 */
public class MQTTSubscriberService {

    private static final Logger log = LoggerFactory.getLogger(MQTTSubscriberService.class);

    // Dirección del broker MQTT
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    // ID del cliente para esta conexión
    private static final String CLIENT_ID = "java-smartgrid-subscriber";
    // Topic donde se publican los consumos
    private static final String TOPIC = "smartgrid/consumption";

    private MqttClient client;

    // Motor de decisiones (IA simulada)
    private final SmartGridDecisionEngine ia;

    private final DispositivoRepository dispositivoRepository;

    public MQTTSubscriberService(SmartGridDecisionEngine ia, DispositivoRepository dispositivoRepository) {
        this.ia = ia;
        this.dispositivoRepository = dispositivoRepository;
    }

    /**
     * Inicializa la conexión MQTT y se suscribe al topic indicado.
     */
    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true); // No mantiene estado entre sesiones
            client.connect(options);

            // Suscripción al topic
            client.subscribe(TOPIC, (topic, msg) -> {
                procesarMensaje(new String(msg.getPayload()));
            });


            log.info("✅ Suscrito a MQTT broker en '{}', topic '{}'", BROKER_URL, TOPIC);

        } catch (MqttException e) {
            log.error("❌ Error al conectar con MQTT Broker: {}", e.getMessage(), e);
        }
    }


    public void procesarMensaje(String payload) {
        String[] partes = payload.split(":");
        if (partes.length == 2) {
            String nombre = partes[0];
            double consumo = Double.parseDouble(partes[1]);

            dispositivoRepository.findByNombre(nombre).ifPresentOrElse(dispositivo -> {
                dispositivo.setConsumo(consumo);
                ia.procesarDispositivo(dispositivo);
            }, () -> log.warn("❌ Dispositivo no registrado en DB: {}", nombre));
        }
    }

    /*public void procesarMensaje(String payload) {
        log.info("⚡ Mensaje recibido: {}", payload);
        String[] partes = payload.split(":");
        if (partes.length == 2) {
            String dispositivo = partes[0];
            double consumo = Double.parseDouble(partes[1]);
            ia.procesarConsumo(dispositivo, consumo);
        }
    }*/

}
