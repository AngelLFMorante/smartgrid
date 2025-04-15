package com.smartgrid.controller;

import com.smartgrid.logic.SmartGridDecisionEngine;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DashboardControllerTest {

    @Test
    void testDashboardLoadsDataIntoModel() {
        SmartGridDecisionEngine engine = mock(SmartGridDecisionEngine.class);
        Model model = mock(Model.class);

        when(engine.getDispositivosActivos()).thenReturn(new HashMap<>());

        DashboardController controller = new DashboardController(engine);
        String view = controller.index(model);

        assertEquals("dashboard", view);
        verify(model).addAttribute(eq("dispositivos"), any());
    }

}