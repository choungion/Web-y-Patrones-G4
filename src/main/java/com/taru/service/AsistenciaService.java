
package com.taru.service;

import com.taru.domain.Asistencia;
import com.taru.domain.Estudiante;
import com.taru.repository.AsistenciaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<Asistencia> getAsistencias() {
        return asistenciaRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<Asistencia> getAsistenciasPorEstudiante(
            Estudiante estudiante
    ) {
        return asistenciaRepository
                .findByEstudianteOrderByFechaDesc(estudiante);
    }

    @Transactional(readOnly = true)
    public Optional<Asistencia> getAsistencia(Integer idAsistencia) {
        return asistenciaRepository.findById(idAsistencia);
    }

    @Transactional
    public void save(Asistencia asistencia) {
        asistenciaRepository.save(asistencia);
    }

    @Transactional
    public void delete(Integer idAsistencia) {
        if (!asistenciaRepository.existsById(idAsistencia)) {
            throw new IllegalArgumentException(
                    "La asistencia con ID "
                    + idAsistencia
                    + " no existe."
            );
        }

        asistenciaRepository.deleteById(idAsistencia);
    }

    @Transactional(readOnly = true)
    public boolean existeAsistencia(
            Estudiante estudiante,
            LocalDate fecha
    ) {
        return asistenciaRepository
                .existsByEstudianteAndFecha(estudiante, fecha);
    }
}

