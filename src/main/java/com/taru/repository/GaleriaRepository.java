package com.taru.repository;

import com.taru.domain.Galeria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GaleriaRepository extends JpaRepository<Galeria, Integer> {

    List<Galeria> findByActivoTrueOrderByIdGaleriaDesc();
}