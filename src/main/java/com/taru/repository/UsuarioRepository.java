package com.taru.repository;

import com.taru.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    List<Usuario> findByActivoTrue();

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByUsernameOrCorreo(String username, String correo);

    boolean existsByUsernameOrCorreo(String username, String correo);

    Optional<Usuario> findByEstudiante_IdEstudiante(Integer idEstudiante);

    List<Usuario> findByEstudianteIsNotNull();
}