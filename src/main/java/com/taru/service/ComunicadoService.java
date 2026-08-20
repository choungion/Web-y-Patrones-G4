package com.taru.service;

import com.taru.domain.Comunicado;
import com.taru.domain.Estudiante;
import com.taru.repository.ComunicadoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;

    public ComunicadoService(ComunicadoRepository comunicadoRepository) {
        this.comunicadoRepository = comunicadoRepository;
    }

    public Comunicado save(Comunicado comunicado) {
        comunicado.setFechaEnvio(LocalDateTime.now());
        return comunicadoRepository.save(comunicado);
    }

    public List<Comunicado> getComunicados() {
        return comunicadoRepository.findAll();
    }

    public List<Comunicado> getComunicadosPorEstudiante(Estudiante estudiante) {
        return comunicadoRepository
                .findByEstudianteOrderByFechaEnvioDesc(estudiante);
    }
}
