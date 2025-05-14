package com.smartgrid.config;

import com.smartgrid.analysis.EnergyAnomalyDetector;
import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.repository.DispositivoRepository;
import com.smartgrid.repository.IncidenciaRepository;
import com.smartgrid.service.MQTTSubscriberService;
import com.smartgrid.service.MedicionService;
import com.smartgrid.service.PrediccionIAService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Mock
    MedicionService medicionService;

    @Mock
    PrediccionIAService prediccionIAService;

    @Test
    public void testDecisionEngineBean() {
        Config config = new Config();
        SmartGridDecisionEngine engine = config.decisionEngine(medicionService, prediccionIAService);

        assertNotNull(engine, "El bean SmartGridDecisionEngine no debe ser null");
    }

    @Test
    public void testMQTTSubscriberServiceBean() {
        Config config = new Config();

        MQTTConfig mqttConfig = mock(MQTTConfig.class);
        SmartGridDecisionEngine engine = mock(SmartGridDecisionEngine.class);
        DispositivoRepository repository = mock(DispositivoRepository.class);
        EnergyAnomalyDetector energyAnomalyDetector = mock(EnergyAnomalyDetector.class);
        IncidenciaRepository incidencia = mock(IncidenciaRepository.class);
        MedicionService medicionService = mock(MedicionService.class);

        MQTTSubscriberService service = config.mqttSubscriberService(
                mqttConfig,
                engine,
                repository,
                energyAnomalyDetector,
                incidencia,
                medicionService
        );

        assertNotNull(service, "El bean MQTTSubscriberService no debe ser null");
    }
}
