package com.taru.repository;

import com.taru.domain.Estudiante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
    public List<Estudiante> findByActivoTrue();

    public List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    public List<Estudiante> findByCursoIsNotNull();
    
    List<Estudiante> findByCursoIdCurso(Integer idCurso);
}