package com.taru.repository;

import com.taru.domain.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    List<Inscripcion> findByEstado(String estado);

    List<Inscripcion> findByEstudiante_IdEstudianteAndEstado(Integer idEstudiante, String estado);

    void deleteByEstudiante_IdEstudiante(Integer idEstudiante);
    
}