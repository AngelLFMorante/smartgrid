package com.smartgrid.service;

import com.smartgrid.config.MQTTConfig;
import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.repository.DispositivoRepository;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la conexión MQTT y del procesamiento de los mensajes recibidos.
 * Escucha mensajes de consumo energético desde un topic, los interpreta y aplica la lógica de decisión.
 */
@Service
public class MQTTSubscriberService {

    private static final Logger log = LoggerFactory.getLogger(MQTTSubscriberService.class);

    /** Configuración del cliente MQTT (URL, clientId, topic, etc.) */
    private final MQTTConfig mqttConfig;

    /** Cliente MQTT que se conecta al broker */
    private MqttClient client;

    /** Motor que toma decisiones en función del consumo */
    private final SmartGridDecisionEngine ia;

    /** Repositorio para acceder a los dispositivos registrados */
    private final DispositivoRepository dispositivoRepository;

    /** Para evitar inicializaciones múltiples si el contexto Spring recarga el bean */
    private boolean alreadyInitialized = false;

    /**
     * Constructor del servicio.
     *
     * @param mqttConfig configuración del broker MQTT
     * @param ia instancia del motor de decisiones
     * @param dispositivoRepository acceso a los dispositivos registrados
     */
    @Autowired
    public MQTTSubscriberService(MQTTConfig mqttConfig, SmartGridDecisionEngine ia, DispositivoRepository dispositivoRepository) {
        this.mqttConfig = mqttConfig;
        this.ia = ia;
        this.dispositivoRepository = dispositivoRepository;
    }

    /**
     * Inicializa la conexión con el broker MQTT después de que el bean ha sido construido.
     * Se suscribe al topic configurado y establece el callback para procesar los mensajes.
     */
    @PostConstruct
    public void init() {
        if (alreadyInitialized) return;
        alreadyInitialized = true;

        try {
            String brokerUrl = mqttConfig.getBrokerUrl();   // Dirección del broker
            String clientId = mqttConfig.getClientId();     // Identificador único del cliente
            String topic = mqttConfig.getTopic();           // Topic de suscripción

            client = new MqttClient(brokerUrl, clientId);

            // Configuramos la conexión
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);         // Evita recibir mensajes antiguos
            options.setKeepAliveInterval(30);      // Ping al broker cada 30 segundos

            // Conexión al broker y suscripción al topic
            client.connect(options);
            client.subscribe(topic, (topicSus, msg) -> {
                procesarMensaje(new String(msg.getPayload())); // Callback para cada mensaje recibido
            });

            log.info("✅ Suscrito a MQTT broker en '{}', topic '{}'", brokerUrl, topic);
            log.info("✅ Conexión exitosa al broker MQTT '{}'", brokerUrl);

        } catch (MqttException e) {
            log.error("❌ Error al conectar con MQTT Broker: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa los mensajes entrantes del broker MQTT.
     * El formato esperado es: "nombre_dispositivo:consumo"
     *
     * @param payload contenido del mensaje recibido
     */
    public void procesarMensaje(String payload) {
        log.info("⚡ Mensaje recibido: '{}'", payload);

        // Validar formato
        if (!payload.contains(":")) {
            log.warn("❌ Formato de mensaje inválido: '{}'. Se esperaba 'nombre:valor'", payload);
            return;
        }

        String[] partes = payload.split(":");
        if (partes.length != 2) {
            log.warn("❌ Formato incompleto. Payload: '{}'", payload);
            return;
        }

        String nombre = partes[0].trim().toLowerCase();
        double consumo;

        try {
            consumo = Double.parseDouble(partes[1].trim());
        } catch (NumberFormatException e) {
            log.error("❌ Valor de consumo inválido: '{}'", partes[1]);
            return;
        }

        // Buscar en la base de datos
        dispositivoRepository.findByNombre(nombre).ifPresentOrElse(dispositivo -> {
            dispositivo.setConsumo(consumo);
            ia.procesarDispositivo(dispositivo);
        }, () -> log.warn("❌ Dispositivo desconocido '{}'. Debe estar registrado.", nombre));
    }

    /**
     * Cierra la conexión MQTT de forma segura al finalizar la aplicación.
     * Esto se ejecuta automáticamente cuando el contenedor de Spring destruye el bean.
     */
    @PreDestroy
    public void cerrarConexion() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                client.close(); // Cierra también el socket
                log.info("🔌 Conexión MQTT cerrada correctamente.");
            } catch (MqttException e) {
                log.warn("⚠️ Error al cerrar la conexión MQTT: {}", e.getMessage(), e);
            }
        }
    }

}
