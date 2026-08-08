package com.taru.service;

import com.taru.domain.Nosotros;
import com.taru.repository.NosotrosRepository;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NosotrosService {

    private final NosotrosRepository nosotrosRepository;
    private final FirebaseStorageService firebaseStorageService;

    public NosotrosService(NosotrosRepository nosotrosRepository, FirebaseStorageService firebaseStorageService) {
        this.nosotrosRepository = nosotrosRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Nosotros> getRegistros() {
        return nosotrosRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Nosotros getRegistro(Integer idNosotros) {
        return nosotrosRepository.findById(idNosotros)
                .orElseThrow(() -> new NoSuchElementException("El registro de Nosotros con ID " + idNosotros + " no existe."));
    }

    @Transactional(readOnly = true)
    public Optional<Nosotros> getActivo() {
        return nosotrosRepository.findFirstByActivoTrueOrderByIdNosotrosDesc();
    }

    @Transactional
    public void save(Nosotros nosotros, MultipartFile fotoFile) {
        nosotrosRepository.save(nosotros);
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(fotoFile, "nosotros", nosotros.getIdNosotros());
                nosotros.setRutaImagen(ruta);
                nosotrosRepository.save(nosotros);
            } catch (IOException e) {
                // Se ignora: el registro queda guardado aunque falle la subida de la imagen.
            }
        }
    }

    @Transactional
    public void delete(Integer idNosotros) {
        if (!nosotrosRepository.existsById(idNosotros)) {
            throw new IllegalArgumentException("El registro con ID " + idNosotros + " no existe.");
        }
        try {
            nosotrosRepository.deleteById(idNosotros);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se pudo eliminar el registro de Nosotros.", e);
        }
    }
}