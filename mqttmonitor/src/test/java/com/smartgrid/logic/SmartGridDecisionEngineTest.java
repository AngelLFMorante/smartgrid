package com.smartgrid.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SmartGridDecisionEngineTest {

    private SmartGridDecisionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new SmartGridDecisionEngine();
    }

    @Test
    void testProcesarConsumo_NoExcedeLimite() {
        engine.procesarConsumo("lavadora", 1000);
        engine.procesarConsumo("tv", 1500);

        Map<String, Double> dispositivos = engine.getDispositivosActivos();
        assertEquals(2, dispositivos.size());
        assertTrue(dispositivos.containsKey("lavadora"));
        assertTrue(dispositivos.containsKey("tv"));
    }

    @Test
    void testProcesarConsumo_ExcedeLimite_EliminaMayor() {
        engine.procesarConsumo("lavadora", 3000);
        engine.procesarConsumo("tv", 2500);

        Map<String, Double> dispositivos = engine.getDispositivosActivos();
        assertEquals(1, dispositivos.size());
        assertFalse(dispositivos.containsKey("lavadora")); // ✅ lavadora es la que se apaga
    }
}