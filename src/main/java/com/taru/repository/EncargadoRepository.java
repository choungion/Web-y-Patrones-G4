package com.taru.repository;

import com.taru.domain.Encargado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncargadoRepository extends JpaRepository<Encargado, Integer> {
    public Optional<Encargado> findByCorreoIgnoreCase(String correo);
}
