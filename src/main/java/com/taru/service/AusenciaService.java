package com.taru.service;

import com.taru.domain.Ausencia;
import com.taru.repository.AusenciaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;

    public AusenciaService(AusenciaRepository ausenciaRepository) {
        this.ausenciaRepository = ausenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAllByOrderByFechaRegistroDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Ausencia> getAusencia(Integer idAusencia) {
        return ausenciaRepository.findById(idAusencia);
    }

    @Transactional
    public Ausencia save(Ausencia ausencia) {

        if (ausencia.getFechaRegistro() == null) {
            ausencia.setFechaRegistro(LocalDateTime.now());
        }

        return ausenciaRepository.save(ausencia);
    }
}
