package com.taru.service;

import com.taru.domain.Curso;
import com.taru.repository.CursoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public CursoService(CursoRepository cursoRepository, FirebaseStorageService firebaseStorageService) {
        this.cursoRepository = cursoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }


    @Transactional(readOnly = true)
    public List<Curso> getCursos(boolean soloActivos) {
        if (soloActivos) {
            return cursoRepository.findByActivoTrue();
        }
        return cursoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Curso> getCurso(Integer idCurso) {
        return cursoRepository.findById(idCurso);
    }
    
     @Transactional
    public void save(Curso curso, MultipartFile fotoFile) {
        cursoRepository.save(curso);
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(fotoFile, "curso", curso.getIdCurso());
                curso.setRutaImagen(ruta);
                cursoRepository.save(curso);
            } catch (IOException e) {
            }
        }
    }

    @Transactional
    public void delete(Integer idCurso) {
        if (!cursoRepository.existsById(idCurso)) {
            throw new IllegalArgumentException("El curso con ID " + idCurso + " no existe.");
        }
        try {
            cursoRepository.deleteById(idCurso);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el curso, tiene registros asociados que no se pueden borrar automaticamente.");
        }
    }
}
