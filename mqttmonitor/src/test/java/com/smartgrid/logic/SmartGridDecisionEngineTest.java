package com.smartgrid.logic;

import com.smartgrid.model.Dispositivo;
import com.smartgrid.model.NivelCriticidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*class SmartGridDecisionEngineTest {

    private SmartGridDecisionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new SmartGridDecisionEngine();
    }

    private Dispositivo crearDispositivo(String nombre, double consumo, NivelCriticidad criticidad) {
        Dispositivo d = new Dispositivo();
        d.setNombre(nombre);
        d.setConsumo(consumo);
        d.setCriticidad(criticidad);
        return d;
    }

    @Test
    void testConsumoDentroDelLimite_NoSeEliminaNada() {
        engine.procesarDispositivo(crearDispositivo("tv", 1000, NivelCriticidad.MEDIA));
        engine.procesarDispositivo(crearDispositivo("lampara", 500, NivelCriticidad.BAJA));

        Map<String, Double> activos = engine.getDispositivosActivos();
        assertEquals(2, activos.size());
    }

    @Test
    void testConsumoExcedido_SeDesconectanNoCriticos() {
        engine.procesarDispositivo(crearDispositivo("lavadora", 2500, NivelCriticidad.BAJA));
        engine.procesarDispositivo(crearDispositivo("tv", 2000, NivelCriticidad.MEDIA));
        engine.procesarDispositivo(crearDispositivo("horno", 1000, NivelCriticidad.CRITICA)); // Total: 5500W

        Map<String, Double> activos = engine.getDispositivosActivos();
        assertEquals(2, activos.size()); // Se debe haber eliminado al menos uno de los no críticos
        assertTrue(activos.containsKey("horno"));
    }

    @Test
    void testSoloCriticosSuperanLimite_NoSePuedeDesconectar() {
        engine.procesarDispositivo(crearDispositivo("respirador", 3000, NivelCriticidad.CRITICA));
        engine.procesarDispositivo(crearDispositivo("bomba de agua", 2500, NivelCriticidad.CRITICA)); // Total: 5500

        Map<String, Double> activos = engine.getDispositivosActivos();
        assertEquals(2, activos.size()); // No se puede eliminar nada
        assertTrue(activos.containsKey("respirador"));
    }

    @Test
    void testDesconectarJustoLoNecesario_HastaLimite() {
        engine.procesarDispositivo(crearDispositivo("baja1", 400, NivelCriticidad.BAJA));
        engine.procesarDispositivo(crearDispositivo("media1", 500, NivelCriticidad.MEDIA));
        engine.procesarDispositivo(crearDispositivo("critico1", 4500, NivelCriticidad.CRITICA)); // Total: 5400

        Map<String, Double> activos = engine.getDispositivosActivos();

        // ✅ Solo debe quedar el crítico si es necesario eliminar más de uno
        assertEquals(2, activos.size());
        assertTrue(activos.containsKey("critico1"));
    }


    @Test
    void testDesconexionManualFunciona() {
        Dispositivo d = crearDispositivo("lavadora", 1000, NivelCriticidad.BAJA);
        engine.procesarDispositivo(d);

        assertTrue(engine.getDispositivosActivos().containsKey("lavadora"));

        engine.desconectarDispositivo("lavadora");
        assertFalse(engine.getDispositivosActivos().containsKey("lavadora"));
    }
}*/
