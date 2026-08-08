package com.taru.repository;

import com.taru.domain.Nosotros;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NosotrosRepository extends JpaRepository<Nosotros, Integer> {

    Optional<Nosotros> findFirstByActivoTrueOrderByIdNosotrosDesc();
}