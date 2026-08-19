package com.taru.service;

import com.taru.domain.Galeria;
import com.taru.repository.GaleriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GaleriaService {

    private final GaleriaRepository galeriaRepository;
    private final FirebaseStorageService firebaseStorageService;

    public GaleriaService(GaleriaRepository galeriaRepository, FirebaseStorageService firebaseStorageService) {
        this.galeriaRepository = galeriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Galeria> getImagenes(boolean soloActivas) {
        if (soloActivas) {
            return galeriaRepository.findByActivoTrueOrderByIdGaleriaDesc();
        }
        return galeriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Galeria> getImagen(Integer idGaleria) {
        return galeriaRepository.findById(idGaleria);
    }

    @Transactional
    public void save(Galeria galeria, MultipartFile fotoFile) {
        galeriaRepository.save(galeria);
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(fotoFile, "galeria", galeria.getIdGaleria());
                galeria.setRutaImagen(ruta);
                galeriaRepository.save(galeria);
            } catch (IOException e) {
                // Se ignora: el registro queda guardado aunque falle la subida de la imagen.
            }
        }
    }

    @Transactional
    public void delete(Integer idGaleria) {
        if (!galeriaRepository.existsById(idGaleria)) {
            throw new IllegalArgumentException("La imagen con ID " + idGaleria + " no existe.");
        }
        try {
            galeriaRepository.deleteById(idGaleria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se pudo eliminar la imagen de la galeria.", e);
        }
    }
}