package com.taru.service;

import com.taru.domain.Ausencia;
import com.taru.repository.AusenciaRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final CorreoMensualidadService correoMensualidadService;

    @Value("${taru.correo.admin}")
    private String correoAdministradora;

    public AusenciaService(AusenciaRepository ausenciaRepository, CorreoMensualidadService correoMensualidadService) {
        this.ausenciaRepository = ausenciaRepository;
        this.correoMensualidadService = correoMensualidadService;
    }

    @Transactional(readOnly = true)
    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAllByOrderByFechaRegistroDesc();
    }

    @Transactional(readOnly = true)
    public List<Ausencia> getAusenciasPorEstudiante(Integer idEstudiante) {
        return ausenciaRepository.findByEstudiante_IdEstudianteOrderByFechaRegistroDesc(idEstudiante);
    }

    @Transactional(readOnly = true)
    public Optional<Ausencia> getAusencia(Integer idAusencia) {
        return ausenciaRepository.findById(idAusencia);
    }

    @Transactional
    public Ausencia save(Ausencia ausencia) {

        if (ausencia.getFechaRegistro() == null) {
            ausencia.setFechaRegistro(LocalDateTime.now());
        }

        Ausencia guardada = ausenciaRepository.save(ausencia);

        enviarCorreoNuevaAusencia(guardada);

        return guardada;
    }

    private void enviarCorreoNuevaAusencia(Ausencia ausencia) {

        if (correoAdministradora == null
                || correoAdministradora.isBlank()) {
            return;
        }

        String asunto = "Nueva ausencia reportada - Centro de Artes Taru";

        String contenido = """
        <html>
            <body>
                <h2>Nueva ausencia reportada</h2>

                <p>
                    Se ha registrado una nueva ausencia
                    en el sistema.
                </p>

                <p>
                    <strong>Estudiante:</strong> %s %s
                </p>

                <p>
                    <strong>Fecha de ausencia:</strong> %s
                </p>

                <p>
                    <strong>Fecha de registro:</strong> %s
                </p>

                <p>
                    <strong>Motivo:</strong> %s
                </p>

                <p>
                    Centro de Artes Taru
                </p>
            </body>
        </html>
        """.formatted(
                ausencia.getEstudiante().getNombre(),
                ausencia.getEstudiante().getApellido(),
                ausencia.getFechaAusencia(),
                ausencia.getFechaRegistro(),
                ausencia.getMotivo()
        );

        try {

            correoMensualidadService.enviarCorreoHtml(
                    correoAdministradora,
                    asunto,
                    contenido
            );

        } catch (MessagingException e) {

            System.err.println(
                    "No se pudo enviar el correo de ausencia: "
                    + e.getMessage()
            );
        }
    }
}
