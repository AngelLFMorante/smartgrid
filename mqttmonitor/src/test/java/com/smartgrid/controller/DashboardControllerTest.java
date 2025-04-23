package com.smartgrid.controller;

import com.smartgrid.logic.SmartGridDecisionEngine;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DashboardControllerTest {

    private DashboardController controller;
    private SmartGridDecisionEngine ia;
    private Model model;

    @BeforeEach
    public void setup() {
        ia = mock(SmartGridDecisionEngine.class);
        controller = new DashboardController(ia);
        model = mock(Model.class);
    }

    @Test
    public void testIndex() {
        when(ia.getDispositivosActivos()).thenReturn(List.of());
        when(ia.isAlertaCriticos()).thenReturn(false);
        when(ia.getLimiteConsumo()).thenReturn(5000.0);
        when(ia.getConsumoTotal()).thenReturn(3200.0);

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
        verify(ia).desconectarDispositivo("tv");
        assertEquals("redirect:/gestion", result);
    }

    @Test
    public void testAjustarPotencia_Success() {
        when(ia.ajustarPotenciaDispositivo("nevera", 1200)).thenReturn(true);
        String result = controller.ajustarPotencia("nevera", 1200);
        assertEquals("redirect:/gestion", result);
    }

    @Test
    public void testAjustarPotencia_Failure() {
        when(ia.ajustarPotenciaDispositivo("nevera", 8000)).thenReturn(false);
        String result = controller.ajustarPotencia("nevera", 8000);
        assertEquals("redirect:/gestion?error=No se pudo ajustar la potencia", result);
    }
}