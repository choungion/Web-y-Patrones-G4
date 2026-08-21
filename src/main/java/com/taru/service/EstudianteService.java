package com.taru.service;

import com.taru.domain.Estudiante;
import com.taru.domain.Inscripcion;
import com.taru.repository.AsistenciaRepository;
import com.taru.repository.AusenciaRepository;
import com.taru.repository.ComunicadoRepository;
import com.taru.repository.EstudianteRepository;
import com.taru.repository.InscripcionRepository;
import com.taru.repository.MensualidadRepository;
import com.taru.repository.UsuarioRepository;
import java.io.IOException;
import java.time.LocalDate;
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
    private final AsistenciaRepository asistenciaRepository;
    private final AusenciaRepository ausenciaRepository;
    private final ComunicadoRepository comunicadoRepository;
    private final MensualidadRepository mensualidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final FirebaseStorageService firebaseStorageService;

    public EstudianteService(EstudianteRepository estudianteRepository,
            InscripcionRepository inscripcionRepository,
            AsistenciaRepository asistenciaRepository,
            AusenciaRepository ausenciaRepository,
            ComunicadoRepository comunicadoRepository,
            MensualidadRepository mensualidadRepository,
            UsuarioRepository usuarioRepository,
            FirebaseStorageService firebaseStorageService) {
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.ausenciaRepository = ausenciaRepository;
        this.comunicadoRepository = comunicadoRepository;
        this.mensualidadRepository = mensualidadRepository;
        this.usuarioRepository = usuarioRepository;
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
        if (estudiante.getCurso() != null) {
            boolean yaInscrito = inscripcionRepository.existsByEstudiante_IdEstudianteAndCurso_IdCurso(
                    estudiante.getIdEstudiante(), estudiante.getCurso().getIdCurso());

            if (!yaInscrito) {
                Inscripcion inscripcion = new Inscripcion();
                inscripcion.setEstudiante(estudiante);
                inscripcion.setCurso(estudiante.getCurso());
                inscripcion.setEstado("Confirmada");
                inscripcion.setFechaInscripcion(LocalDate.now());
                inscripcionRepository.save(inscripcion);
            }
        }
    }

    @Transactional
    public void delete(Integer idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new IllegalArgumentException("El estudiante con ID " + idEstudiante + " no existe.");
        }

        boolean tieneMensualidades = !mensualidadRepository
                .findByInscripcionEstudianteIdEstudianteOrderByFechaVencimientoDesc(idEstudiante)
                .isEmpty();

        if (tieneMensualidades) {
            throw new IllegalStateException(
                    "No se puede eliminar: el estudiante tiene mensualidades/pagos en su historial. "
                    + "Para conservar ese historial, marcalo como inactivo en vez de eliminarlo."
            );
        }

        try {
            usuarioRepository.findByEstudiante_IdEstudiante(idEstudiante).ifPresent(usuario -> {
                usuario.setEstudiante(null);
                usuarioRepository.save(usuario);
            });

            asistenciaRepository.deleteByEstudiante_IdEstudiante(idEstudiante);
            ausenciaRepository.deleteByEstudiante_IdEstudiante(idEstudiante);
            comunicadoRepository.deleteByEstudiante_IdEstudiante(idEstudiante);
            inscripcionRepository.deleteByEstudiante_IdEstudiante(idEstudiante);

            asistenciaRepository.flush();
            ausenciaRepository.flush();
            comunicadoRepository.flush();
            inscripcionRepository.flush();

            estudianteRepository.deleteById(idEstudiante);
            estudianteRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el estudiante, tiene registros asociados que no se pueden borrar automaticamente.");
        }
    }
}