
package com.taru.repository;

import com.taru.domain.Inscripcion;
import com.taru.domain.Mensualidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensualidadRepository extends JpaRepository<Mensualidad, Integer>  {
    
    boolean existsByInscripcionAndPeriodo(
            Inscripcion inscripcion,
            String periodo);

    Optional<Mensualidad> findByInscripcionAndPeriodo(
            Inscripcion inscripcion,
            String periodo);

    List<Mensualidad> findByInscripcionOrderByPeriodoAsc
        ( Inscripcion inscripcion);

    List<Mensualidad> findByEstadoOrderByFechaVencimientoDesc
        (Mensualidad.EstadoMensualidad estado);
    
   
    List<Mensualidad> findByInscripcionEstudianteIdEstudianteOrderByFechaVencimientoDesc
        (Integer idEstudiante);

    
    List<Mensualidad> findByInscripcionEstudianteIdEstudianteAndEstadoOrderByFechaVencimientoDesc
        ( Integer idEstudiante,
            Mensualidad.EstadoMensualidad estado);
    
    List<Mensualidad> findAllByOrderByFechaVencimientoDesc();
        
}
