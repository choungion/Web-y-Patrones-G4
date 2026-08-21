
package com.taru.repository;

import com.taru.domain.Asistencia;
import com.taru.domain.Estudiante;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepository
        extends JpaRepository<Asistencia, Integer> {

    List<Asistencia> findByEstudianteOrderByFechaDesc(
            Estudiante estudiante
    );

    List<Asistencia> findAllByOrderByFechaDesc();

    boolean existsByEstudianteAndFecha(
            Estudiante estudiante,
            LocalDate fecha
    );

    void deleteByEstudiante_IdEstudiante(Integer idEstudiante);
}