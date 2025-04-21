package com.smartgrid.service;

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

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setKeepAliveInterval(30); // importante para mantener la sesión activa

            // Configura el callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("❌ Conexión MQTT perdida: {}", cause.getMessage());
                    boolean reconnectado = false;
                    while (!reconnectado) {
                        try {
                            Thread.sleep(5000); // espera 5 segundos antes de reintentar
                            client.connect(); // intenta reconectar
                            client.subscribe(TOPIC);
                            reconnectado = true;
                            log.info("🔁 Reconexion exitosa al MQTT broker.");
                        } catch (Exception e) {
                            log.warn("❌ Reintento fallido: {}", e.getMessage());
                        }
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    log.info("⚡ Mensaje recibido: {}", payload);
                    procesarMensaje(payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // No se utiliza en modo solo suscripción
                }
            });

            client.connect(options);
            client.subscribe(TOPIC);

            log.info("✅ Suscrito a MQTT broker en '{}', topic '{}'", BROKER_URL, TOPIC);

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

    /**
     * Inicializa la conexión MQTT y se suscribe al topic indicado.
     */
   /* @PostConstruct
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
            String nombre = partes[0].trim().toLowerCase(); // más robusto
            double consumo = Double.parseDouble(partes[1]);

            dispositivoRepository.findByNombre(nombre).ifPresentOrElse(dispositivo -> {
                dispositivo.setConsumo(consumo);
                ia.procesarDispositivo(dispositivo);
            }, () -> log.warn("❌ Dispositivo desconocido '{}'. Debe ser registrado antes de usar.", nombre));
        } else {
            log.warn("❌ Formato de mensaje inválido: '{}'", payload);
        }
    }*/


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
