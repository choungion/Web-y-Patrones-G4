package com.taru.repository;

import com.taru.domain.Ausencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, Integer> {

    List<Ausencia> findAllByOrderByFechaRegistroDesc();

    List<Ausencia> findByEstudiante_IdEstudianteOrderByFechaRegistroDesc(Integer idEstudiante);

    void deleteByEstudiante_IdEstudiante(Integer idEstudiante);

}