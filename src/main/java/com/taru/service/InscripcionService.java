package com.taru.service;

import com.taru.domain.Inscripcion;
import com.taru.repository.InscripcionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> getInscripciones() {
        return inscripcionRepository.findAll();
    }

    @Transactional
    public void save(Inscripcion inscripcion) {
        if (inscripcion.getEstado() == null || inscripcion.getEstado().isBlank()) {
            inscripcion.setEstado("Pendiente");
        }
        if (inscripcion.getFechaInscripcion() == null) {
            inscripcion.setFechaInscripcion(LocalDate.now());
        }
        inscripcionRepository.save(inscripcion);
    }
}
