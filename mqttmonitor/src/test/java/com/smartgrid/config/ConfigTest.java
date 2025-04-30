package com.smartgrid.config;

import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.repository.DispositivoRepository;
import com.smartgrid.service.MQTTSubscriberService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void testDecisionEngineBean() {
        Config config = new Config();
        SmartGridDecisionEngine engine = config.decisionEngine();

        assertNotNull(engine, "El bean SmartGridDecisionEngine no debe ser null");
    }

    @Test
    public void testMQTTSubscriberServiceBean() {
        Config config = new Config();

        MQTTConfig mqttConfig = mock(MQTTConfig.class);
        SmartGridDecisionEngine engine = mock(SmartGridDecisionEngine.class);
        DispositivoRepository repository = mock(DispositivoRepository.class);

        MQTTSubscriberService service = config.mqttSubscriberService(mqttConfig, engine, repository);

        assertNotNull(service, "El bean MQTTSubscriberService no debe ser null");
    }
}
