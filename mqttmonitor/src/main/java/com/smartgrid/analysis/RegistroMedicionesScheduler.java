package com.smartgrid.analysis;

import com.smartgrid.logic.SmartGridDecisionEngine;
import com.smartgrid.model.Dispositivo;
import com.smartgrid.service.MedicionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RegistroMedicionesScheduler {

    private final SmartGridDecisionEngine decisionEngine;
    private final MedicionService medicionService;

    public RegistroMedicionesScheduler(SmartGridDecisionEngine decisionEngine, MedicionService medicionService) {
        this.decisionEngine = decisionEngine;
        this.medicionService = medicionService;
    }

    @Scheduled(fixedRate = 60000) // cada 1 minuto (para pruebas)
    public void registrarMediciones() {
        for (Dispositivo dispositivo : decisionEngine.getDispositivosActivos()) {
            medicionService.registrar(dispositivo.getNombre(), dispositivo.getConsumo());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")// cada día a las 3 AM
    public void eliminarAntiguas() {
        medicionService.eliminarAntiguas();
    }
}
