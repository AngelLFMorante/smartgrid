package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmartGridDecisionEngineTest {

    private SmartGridDecisionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new SmartGridDecisionEngine();
    }

    @Test
    void testProcesarDispositivoDentroDelLimite() {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setNombre("TV");
        dispositivo.setCriticidad(NivelCriticidad.BAJA);
        dispositivo.setConsumo(1000);

        engine.procesarDispositivo(dispositivo);

        assertFalse(engine.isAlertaCriticos());
        assertEquals(1000, engine.getConsumoTotal());
        assertEquals(1, engine.getDispositivosActivos().size());
    }

    @Test
    void testDesconectaNoCriticosCuandoSeExcede() {
        // Añadimos varios dispositivos
        for (int i = 1; i <= 6; i++) {
            Dispositivo d = new Dispositivo();
            d.setNombre("Dispositivo" + i);
            d.setCriticidad(NivelCriticidad.MEDIA);
            d.setConsumo(1000);
            engine.procesarDispositivo(d);
        }

        assertFalse(engine.isAlertaCriticos());
        assertTrue(engine.getConsumoTotal() <= engine.getLimiteConsumo());
    }

    @Test
    void testAlertaCuandoCriticosSuperanLimite() {
        Dispositivo critico1 = new Dispositivo();
        critico1.setNombre("Servidor");
        critico1.setCriticidad(NivelCriticidad.CRITICA);
        critico1.setConsumo(6000); // mayor que el límite

        engine.procesarDispositivo(critico1);

        assertTrue(engine.isAlertaCriticos());
        assertEquals(6000, engine.getConsumoTotal());
    }

    @Test
    void testAjustarPotenciaDispositivoCritico_Exito() {
        Dispositivo critico = new Dispositivo();
        critico.setNombre("Ventilador");
        critico.setCriticidad(NivelCriticidad.CRITICA);
        critico.setConsumo(6000); // Inicialmente muy alto

        engine.procesarDispositivo(critico);

        boolean result = engine.ajustarPotenciaDispositivo("Ventilador", 3000);
        assertTrue(result);
        assertFalse(engine.isAlertaCriticos());
        assertTrue(engine.getConsumoTotal() <= engine.getLimiteConsumo());
    }

    @Test
    void testAjustarPotenciaDispositivoCritico_Fallo() {
        Dispositivo critico = new Dispositivo();
        critico.setNombre("Refrigerador");
        critico.setCriticidad(NivelCriticidad.CRITICA);
        critico.setConsumo(6000);

        engine.procesarDispositivo(critico);

        boolean result = engine.ajustarPotenciaDispositivo("Refrigerador", 6000);
        assertFalse(result);
    }

    @Test
    void testDesconectarDispositivo() {
        Dispositivo d = new Dispositivo();
        d.setNombre("Cafetera");
        d.setCriticidad(NivelCriticidad.BAJA);
        d.setConsumo(500);

        engine.procesarDispositivo(d);
        engine.desconectarDispositivo("Cafetera");

        assertTrue(engine.getDispositivosActivos().isEmpty());
    }
}
