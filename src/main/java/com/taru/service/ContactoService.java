package com.taru.service;

import com.taru.domain.Contacto;
import com.taru.repository.ContactoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<Contacto> getRegistros() {
        return contactoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Contacto getRegistro(Integer idContacto) {
        return contactoRepository.findById(idContacto)
                .orElseThrow(() -> new NoSuchElementException("El registro de Contacto con ID " + idContacto + " no existe."));
    }

    @Transactional(readOnly = true)
    public Optional<Contacto> getActivo() {
        return contactoRepository.findFirstByActivoTrueOrderByIdContactoDesc();
    }

    @Transactional
    public void save(Contacto contacto) {
        contactoRepository.save(contacto);
    }

    @Transactional
    public void delete(Integer idContacto) {
        if (!contactoRepository.existsById(idContacto)) {
            throw new IllegalArgumentException("El registro con ID " + idContacto + " no existe.");
        }
        try {
            contactoRepository.deleteById(idContacto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se pudo eliminar el registro de Contacto.", e);
        }
    }
}