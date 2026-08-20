
package com.taru.repository;

import com.taru.domain.Cobro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CobroRepository extends JpaRepository<Cobro, Integer>{
    List<Cobro> findAllByOrderByFechaEnvioDesc();
}
