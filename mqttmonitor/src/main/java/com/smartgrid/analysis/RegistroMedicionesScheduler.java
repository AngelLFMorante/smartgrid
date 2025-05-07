package com.smartgrid.analysis;

import com.smartgrid.repository.DispositivoRepository;
import com.smartgrid.service.MedicionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RegistroMedicionesScheduler {

    private final DispositivoRepository dispositivoRepository;
    private final MedicionService medicionService;

    public RegistroMedicionesScheduler(DispositivoRepository dispositivoRepository, MedicionService medicionService) {
        this.dispositivoRepository = dispositivoRepository;
        this.medicionService = medicionService;
    }

    @Scheduled(fixedRate = 60000) // cada 1 minuto (para pruebas)
    public void registrarMediciones() {
        dispositivoRepository.findAll().forEach(dispositivo -> {
            medicionService.registrar(dispositivo.getNombre(), dispositivo.getConsumo());
        });
    }

    @Scheduled(cron = "0 0 0 1 * *") // limpieza el día 1 de cada mes
    public void eliminarAntiguas() {
        medicionService.eliminarAntiguas();
    }
}
