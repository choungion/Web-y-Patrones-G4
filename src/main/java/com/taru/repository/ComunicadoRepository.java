package com.taru.repository;

import com.taru.domain.Comunicado;
import com.taru.domain.Estudiante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunicadoRepository extends JpaRepository<Comunicado, Integer> {

    List<Comunicado> findByEstudianteOrderByFechaEnvioDesc(Estudiante estudiante);
}
