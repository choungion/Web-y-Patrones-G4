
package com.taru.repository;

import com.taru.domain.Mensualidad;
import com.taru.domain.Pago;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByMensualidad(Mensualidad mensualidad);

    Optional<Pago> findByMensualidad_IdMensualidad(Integer idMensualidad);
}
