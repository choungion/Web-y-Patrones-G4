package com.taru.service;

import com.taru.domain.Comunicado;
import com.taru.domain.Estudiante;
import com.taru.repository.ComunicadoRepository;
import com.taru.repository.EstudianteRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;
    private final EstudianteRepository estudianteRepository;
    private final CorreoMensualidadService correoMensualidadService;

    public ComunicadoService(ComunicadoRepository comunicadoRepository, EstudianteRepository estudianteRepository, CorreoMensualidadService correoMensualidadService) {
        this.comunicadoRepository = comunicadoRepository;
        this.estudianteRepository = estudianteRepository;
        this.correoMensualidadService = correoMensualidadService;
    }

    public void enviarComunicado(
            Comunicado comunicado,
            String tipoDestinatario,
            Integer idEstudiante,
            Integer idCurso) {

        if (tipoDestinatario.equals("ESTUDIANTE")) {

            Estudiante estudiante = estudianteRepository
                    .findById(idEstudiante)
                    .orElseThrow();

            comunicado.setEstudiante(estudiante);
            comunicado.setFechaEnvio(LocalDateTime.now());

            comunicadoRepository.save(comunicado);
            enviarCorreoComunicado(comunicado);
        } else if (tipoDestinatario.equals("CURSO")) {

            List<Estudiante> estudiantes
                    = estudianteRepository.findByCursoIdCurso(idCurso);

            for (Estudiante estudiante : estudiantes) {

                Comunicado nuevoComunicado = new Comunicado();

                nuevoComunicado.setTitulo(comunicado.getTitulo());
                nuevoComunicado.setMensaje(comunicado.getMensaje());
                nuevoComunicado.setFechaEnvio(LocalDateTime.now());
                nuevoComunicado.setEstudiante(estudiante);

                comunicadoRepository.save(nuevoComunicado);
                enviarCorreoComunicado(comunicado);
            }
        } else if (tipoDestinatario.equals("TODOS")) {

            List<Estudiante> estudiantes
                    = estudianteRepository.findAll();

            for (Estudiante estudiante : estudiantes) {

                Comunicado nuevoComunicado = new Comunicado();

                nuevoComunicado.setTitulo(comunicado.getTitulo());
                nuevoComunicado.setMensaje(comunicado.getMensaje());
                nuevoComunicado.setFechaEnvio(LocalDateTime.now());
                nuevoComunicado.setEstudiante(estudiante);

                comunicadoRepository.save(nuevoComunicado);
                enviarCorreoComunicado(comunicado);
            }
        }
    }

    public List<Comunicado> getComunicados() {
        return comunicadoRepository.findAll();
    }

    public List<Comunicado> getComunicadosPorEstudiante(Estudiante estudiante) {
        return comunicadoRepository
                .findByEstudianteOrderByFechaEnvioDesc(estudiante);
    }
    
    private String obtenerCorreoNotificacion(Estudiante estudiante) {

        if (estudiante.getEncargado() != null
                && estudiante.getEncargado().getCorreo() != null
                && !estudiante.getEncargado().getCorreo().isBlank()) {

            return estudiante.getEncargado().getCorreo();
        }

        if (estudiante.getCorreo() != null
                && !estudiante.getCorreo().isBlank()) {

            return estudiante.getCorreo();
        }

        return null;
    }

    private void enviarCorreoComunicado(Comunicado comunicado) {

        Estudiante estudiante = comunicado.getEstudiante();

        String correo = obtenerCorreoNotificacion(estudiante);

        if (correo == null) {
            return;
        }

        String asunto = comunicado.getTitulo();

        String contenido = """
        <html>
            <body>
                <h2>%s</h2>

                <p>Hola %s %s,</p>

                <p>
                    %s
                </p>

                <p>
                    Centro de Artes Taru
                </p>
            </body>
        </html>
        """.formatted(
                comunicado.getTitulo(),
                estudiante.getNombre(),
                estudiante.getApellido(),
                comunicado.getMensaje()
        );

        try {

            correoMensualidadService.enviarCorreoHtml(
                    correo,
                    asunto,
                    contenido
            );

        } catch (MessagingException e) {

            System.err.println(
                    "No se pudo enviar el correo del comunicado: "
                    + e.getMessage()
            );
        }
    }
}
