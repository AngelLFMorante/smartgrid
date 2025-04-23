package com.smartgrid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DispositivoTest {

    @Test
    void testGettersAndSetters() {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setId(1L);
        dispositivo.setNombre("Router");
        dispositivo.setZona("Oficina");
        dispositivo.setCriticidad(NivelCriticidad.MEDIA);
        dispositivo.setConsumo(300);

        assertEquals(1L, dispositivo.getId());
        assertEquals("Router", dispositivo.getNombre());
        assertEquals("Oficina", dispositivo.getZona());
        assertEquals(NivelCriticidad.MEDIA, dispositivo.getCriticidad());
        assertEquals(300, dispositivo.getConsumo());
    }
}
