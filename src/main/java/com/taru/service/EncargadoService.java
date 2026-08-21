package com.taru.service;

import com.taru.domain.Encargado;
import com.taru.repository.EncargadoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncargadoService {

    private final EncargadoRepository encargadoRepository;

    public EncargadoService(EncargadoRepository encargadoRepository) {
        this.encargadoRepository = encargadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Encargado> getEncargados() {
        return encargadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Encargado> getEncargado(Integer idEncargado) {
        return encargadoRepository.findById(idEncargado);
    }

    @Transactional
    public Encargado save(Encargado encargado) {
        return encargadoRepository.save(encargado);
    }

    @Transactional
    public Encargado findOrCreate(String nombre, String telefono, String correo) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        if (correo != null && !correo.isBlank()) {
            Optional<Encargado> existente = encargadoRepository.findByCorreoIgnoreCase(correo);
            if (existente.isPresent()) {
                return existente.get();
            }
        }
        if (telefono != null && !telefono.isBlank()) {
            Optional<Encargado> existentePorTelefono
                    = encargadoRepository.findByNombreIgnoreCaseAndTelefono(nombre, telefono);
            if (existentePorTelefono.isPresent()) {
                return existentePorTelefono.get();
            }
        }
        Encargado nuevo = new Encargado();
        nuevo.setNombre(nombre);
        nuevo.setTelefono(telefono);
        nuevo.setCorreo(correo);
        return encargadoRepository.save(nuevo);
    }
}
