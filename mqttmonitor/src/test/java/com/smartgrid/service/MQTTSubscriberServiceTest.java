package com.smartgrid.service;

import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
import com.smartgrid.repository.DispositivoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class MQTTSubscriberServiceTest {

    private SmartGridDecisionEngine mockIa;
    private DispositivoRepository mockRepo;
    private MQTTSubscriberService service;

    @BeforeEach
    void setUp() {
        mockIa = mock(SmartGridDecisionEngine.class);
        mockRepo = mock(DispositivoRepository.class);
        service = new MQTTSubscriberService(null, mockIa, mockRepo);
    }

    @Test
    void testProcesaMensajeValido_DispositivoExistente() {
        Dispositivo d = new Dispositivo();
        d.setNombre("lavadora");
        d.setCriticidad(NivelCriticidad.MEDIA);

        when(mockRepo.findByNombre("lavadora")).thenReturn(Optional.of(d));

        service.procesarMensaje("lavadora:2100");

        verify(mockIa, times(1)).procesarDispositivo(d);
    }

    @Test
    void testProcesaMensajeInvalido_NoProcesa() {
        service.procesarMensaje("mensaje_erroneo");

        verify(mockIa, never()).procesarDispositivo(any());
    }

    @Test
    void testProcesaMensaje_DispositivoNoRegistrado() {
        when(mockRepo.findByNombre("tv")).thenReturn(Optional.empty());

        service.procesarMensaje("tv:1500");

        verify(mockIa, never()).procesarDispositivo(any());
    }
}
