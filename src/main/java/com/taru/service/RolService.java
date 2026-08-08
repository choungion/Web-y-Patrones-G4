package com.taru.service;

import com.taru.domain.Rol;
import com.taru.repository.RolRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Rol getRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new NoSuchElementException("El rol con ID " + idRol + " no existe."));
    }

    @Transactional
    public void save(Rol rol) {
        rolRepository.save(rol);
    }

    @Transactional
    public void delete(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new IllegalArgumentException("El rol con ID " + idRol + " no existe.");
        }
        try {
            rolRepository.deleteById(idRol);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el rol. Tiene usuarios o rutas asociadas.", e);
        }
    }
}