package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
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

    private Dispositivo crearDispositivo(String nombre, double consumo, NivelCriticidad nivel) {
        Dispositivo d = new Dispositivo();
        d.setNombre(nombre);
        d.setConsumo(consumo);
        d.setCriticidad(nivel);
        return d;
    }

    @Test
    void testNoSeExcedeElLimite() {
        Dispositivo lavadora = crearDispositivo("lavadora", 1000, NivelCriticidad.MEDIA);
        Dispositivo tv = crearDispositivo("tv", 1500, NivelCriticidad.BAJA);

        engine.procesarDispositivo(lavadora);
        engine.procesarDispositivo(tv);

        Map<String, Dispositivo> activos = engine.getDispositivosActivos();
        assertEquals(2, activos.size());
        assertTrue(activos.containsKey("lavadora"));
        assertTrue(activos.containsKey("tv"));
    }

    @Test
    void testSeExcedeLimiteYSeDesactivaElMayorNoCritico() {
        Dispositivo lavadora = crearDispositivo("lavadora", 3000, NivelCriticidad.MEDIA);
        Dispositivo horno = crearDispositivo("horno", 2500, NivelCriticidad.BAJA);

        engine.procesarDispositivo(lavadora);
        engine.procesarDispositivo(horno);

        Map<String, Dispositivo> activos = engine.getDispositivosActivos();
        assertEquals(1, activos.size());
        assertFalse(activos.containsKey("lavadora")); // Elimina al mayor de menor criticidad
    }

    @Test
    void testNoSeDesactivaCriticoSiTodosSonCriticos() {
        Dispositivo cocina = crearDispositivo("cocina", 3000, NivelCriticidad.CRITICA);
        Dispositivo horno = crearDispositivo("horno", 2500, NivelCriticidad.CRITICA);

        engine.procesarDispositivo(cocina);
        engine.procesarDispositivo(horno);

        Map<String, Dispositivo> activos = engine.getDispositivosActivos();
        assertEquals(2, activos.size()); // No elimina críticos por ahora
    }
}
