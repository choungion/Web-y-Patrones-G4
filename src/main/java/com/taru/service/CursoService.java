package com.taru.service;

import com.taru.domain.Curso;
import com.taru.repository.CursoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
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
}
