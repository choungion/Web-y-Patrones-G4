package com.taru.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.taru.service.MensualidadService;


@Component
@RequiredArgsConstructor
public class MensualidadScheduler {
    private final MensualidadService mensualidadService;

    @Scheduled(cron = "0 0 1 * * *")
    public void generarMensualidades() {

        mensualidadService.procesarMensualidades();

    }

}
