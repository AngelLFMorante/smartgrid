package com.smartgrid.controller;

import com.smartgrid.analysis.EnergyAnomalyDetector;
import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.logic.SmartGridService;
import com.smartgrid.service.IncidenciaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DashboardControllerTest {

    private DashboardController controller;
    private SmartGridService service;
    private Model model;
    private EnergyAnomalyDetector detector;
    private IncidenciaService incidenciaService;

    @BeforeEach
    public void setup() {
        service = mock(SmartGridService.class);
        detector = mock(EnergyAnomalyDetector.class);
        incidenciaService = mock(IncidenciaService.class);
        controller = new DashboardController(service,detector,incidenciaService);
        model = mock(Model.class);
    }

    @Test
    public void testIndex() {
        when(service.obtenerDispositivosActivos()).thenReturn(List.of());
        when(service.hayAlertaCriticos()).thenReturn(false);
        when(service.getLimiteConsumo()).thenReturn(5000.0);
        when(service.obtenerConsumoTotal()).thenReturn(3200.0);

        String view = controller.index(model);

        verify(model).addAttribute(eq("dispositivos"), eq(List.of()));
        verify(model).addAttribute("alertaCriticos", false);
        verify(model).addAttribute("limitePermitido", 5000.0);
        verify(model).addAttribute("totalActual", 3200.0);

        assertEquals("dashboard", view);
    }

    @Test
    public void testGestionManual() {
        String view = controller.gestionManual(model);
        verify(model).addAttribute(eq("modoManual"), eq(true));
        assertEquals("gestion", view);
    }

    @Test
    public void testDesconectar() {
        String result = controller.desconectar("tv");
        verify(service).desconectar("tv");
        assertEquals("redirect:/gestion", result);
    }

    @Test
    public void testAjustarPotencservicio_Success() {
        when(service.ajustarPotencia("nevera", 1200)).thenReturn(true);
        String result = controller.ajustarPotencia("nevera", 1200);
        assertEquals("redirect:/gestion", result);
    }

    @Test
    public void testAjustarPotencia_Failure() {
        when(service.ajustarPotencia("nevera", 8000)).thenReturn(false);
        String result = controller.ajustarPotencia("nevera", 8000);
        assertEquals("redirect:/gestion?error=No se pudo ajustar la potencia", result);
    }
}