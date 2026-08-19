package com.taru.repository;

import com.taru.domain.Contacto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {

    Optional<Contacto> findFirstByActivoTrueOrderByIdContactoDesc();
}