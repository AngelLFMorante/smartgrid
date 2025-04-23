package com.smartgrid.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testDashboardViewLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    void testVistaGestionManualConModelo() throws Exception {
        mockMvc.perform(get("/gestion"))
                .andExpect(status().isOk())
                .andExpect(view().name("gestion"))
                .andExpect(model().attributeExists("dispositivos"))
                .andExpect(model().attribute("modoManual", true))
                .andExpect(model().attributeExists("totalActual"))
                .andExpect(model().attributeExists("limitePermitido"));
    }

}
