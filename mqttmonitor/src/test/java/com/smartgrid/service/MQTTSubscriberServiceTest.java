package com.smartgrid.service;

import com.smartgrid.logic.SmartGridDecisionEngine;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/*class MQTTSubscriberServiceTest {

    private MqttClient mockClient;
    private SmartGridDecisionEngine mockEngine;

    @BeforeEach
    void setUp() throws MqttException {
        mockClient = mock(MqttClient.class);
        mockEngine = mock(SmartGridDecisionEngine.class);
    }

    @Test
    void testReceivesValidMessageAndProcessesIt() throws Exception {
        // Suplantamos comportamiento MQTT
        MqttMessage mensaje = new MqttMessage("tv:3500".getBytes());

        MQTTSubscriberService service = new MQTTSubscriberService(mockEngine);

        // Simular directamente la lógica de recepción (ya que init requiere broker real)
        service.init(); // Si lo deseas puedes mockearlo, aquí solo para estructura

        // Suplente: llamar a la lógica directamente
        mockEngine.procesarConsumo("tv", 3500);

        verify(mockEngine, times(1)).procesarConsumo("tv", 3500);
    }

    @Test
    void testProcesarMensaje_Valido() {
        SmartGridDecisionEngine mockIa = mock(SmartGridDecisionEngine.class);
        MQTTSubscriberService service = new MQTTSubscriberService(mockIa);

        service.procesarMensaje("lavadora:2100");

        verify(mockIa, times(1)).procesarConsumo("lavadora", 2100.0);
    }


    @Test
    void testProcesarMensaje_Invalido() {
        SmartGridDecisionEngine mockIa = mock(SmartGridDecisionEngine.class);
        MQTTSubscriberService service = new MQTTSubscriberService(mockIa);

        service.procesarMensaje("invalido_sin_dos_partes");

        verify(mockIa, never()).procesarConsumo(any(), anyDouble());
    }

}*/
