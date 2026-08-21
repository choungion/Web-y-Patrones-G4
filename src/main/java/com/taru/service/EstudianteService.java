package com.taru.service;

import com.taru.domain.Estudiante;
import com.taru.repository.EstudianteRepository;
import com.taru.repository.InscripcionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final FirebaseStorageService firebaseStorageService;

    public EstudianteService(EstudianteRepository estudianteRepository, InscripcionRepository inscripcionRepository, FirebaseStorageService firebaseStorageService) {
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Estudiante> getEstudiantes(boolean soloActivos) {
        if (soloActivos) {
            return estudianteRepository.findByActivoTrue();
        }
        return estudianteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Estudiante> getEstudiantesEnCurso() {
        return estudianteRepository.findByCursoIsNotNull();
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> getEstudiante(Integer idEstudiante) {
        return estudianteRepository.findById(idEstudiante);
    }

    @Transactional(readOnly = true)
    public List<Estudiante> buscar(String texto) {
        return estudianteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto);
    }

    @Transactional
    public void save(Estudiante estudiante, MultipartFile fotoFile) {
        estudianteRepository.save(estudiante);
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(fotoFile, "estudiante", estudiante.getIdEstudiante());
                estudiante.setRutaFoto(ruta);
                estudianteRepository.save(estudiante);
            } catch (IOException e) {
            }
        }
    }

    @Transactional
    public void delete(Integer idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new IllegalArgumentException("El estudiante con ID " + idEstudiante + " no existe.");
        }
        try {
            inscripcionRepository.deleteByEstudiante_IdEstudiante(idEstudiante);
            inscripcionRepository.flush();
            estudianteRepository.deleteById(idEstudiante);
            estudianteRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el estudiante, tiene registros asociados que no se pueden borrar automaticamente.");
        }
    }
}