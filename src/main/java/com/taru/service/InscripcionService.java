package com.taru.service;

import com.taru.domain.Curso;
import com.taru.domain.Inscripcion;
import com.taru.repository.InscripcionRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CorreoMensualidadService correoMensualidadService;

    @Value("${taru.correo.admin}")
    private String correoAdministradora;

    public InscripcionService(
            InscripcionRepository inscripcionRepository,
            CorreoMensualidadService correoMensualidadService) {

        this.inscripcionRepository = inscripcionRepository;
        this.correoMensualidadService = correoMensualidadService;
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> getInscripciones() {
        return inscripcionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Curso> getCursosInscritos(Integer idEstudiante) {
        return inscripcionRepository
                .findByEstudiante_IdEstudianteAndEstado(
                        idEstudiante,
                        "Confirmada")
                .stream()
                .map(Inscripcion::getCurso)
                .distinct()
                .toList();
    }

    @Transactional
    public void save(Inscripcion inscripcion) {

        if (inscripcion.getEstado() == null
                || inscripcion.getEstado().isBlank()) {

            inscripcion.setEstado("Pendiente");
        }

        if (inscripcion.getFechaInscripcion() == null) {
            inscripcion.setFechaInscripcion(LocalDate.now());
        }

        Inscripcion guardada =
                inscripcionRepository.save(inscripcion);

        enviarCorreoNuevaInscripcion(guardada);
    }

    private void enviarCorreoNuevaInscripcion(
            Inscripcion inscripcion) {

        if (correoAdministradora == null
                || correoAdministradora.isBlank()) {
            return;
        }

        String nombreEstudiante =
                inscripcion.getEstudiante().getNombre();

        String apellidoEstudiante =
                inscripcion.getEstudiante().getApellido();

        String nombreCurso =
                inscripcion.getCurso().getNombre();

        String asunto =
                "Nueva inscripción - Centro de Artes Taru";

        String contenido = """
            <html>
                <body>

                    <h2>Nueva inscripción registrada</h2>

                    <p>
                        Se ha registrado una nueva inscripción
                        en el sistema de Centro de Artes Taru.
                    </p>

                    <p>
                        <strong>Estudiante:</strong>
                        %s %s
                    </p>

                    <p>
                        <strong>Curso:</strong>
                        %s
                    </p>

                    <p>
                        <strong>Fecha de inscripción:</strong>
                        %s
                    </p>

                    <p>
                        <strong>Estado:</strong>
                        %s
                    </p>

                    <p>
                        Centro de Artes Taru
                    </p>

                </body>
            </html>
            """.formatted(
                nombreEstudiante,
                apellidoEstudiante,
                nombreCurso,
                inscripcion.getFechaInscripcion(),
                inscripcion.getEstado()
        );

        try {

            correoMensualidadService.enviarCorreoHtml(
                    correoAdministradora,
                    asunto,
                    contenido
            );

        } catch (MessagingException e) {

            System.err.println(
                    "No se pudo enviar el correo de inscripción: "
                    + e.getMessage()
            );
        }
    }
}