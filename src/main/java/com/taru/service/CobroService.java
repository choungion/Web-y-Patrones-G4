
package com.taru.service;

import com.taru.domain.Cobro;
import com.taru.repository.CobroRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CobroService {
    private final CobroRepository cobroRepository;

    @Transactional
    public void registrar(
            Cobro cobro) {

        cobro.setFechaEnvio(LocalDateTime.now());

        cobroRepository.save(cobro);
    }

    @Transactional(readOnly = true)
    public List<Cobro> obtenerCobros() {

        return cobroRepository.findAllByOrderByFechaEnvioDesc();
    }
}
