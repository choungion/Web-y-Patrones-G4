package com.taru.service;

import com.taru.domain.Cobro;
import com.taru.domain.Estudiante;
import com.taru.domain.Inscripcion;
import com.taru.domain.Mensualidad;
import com.taru.repository.InscripcionRepository;
import com.taru.repository.MensualidadRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MensualidadService {

    private final InscripcionRepository inscripcionRepository;
    private final MensualidadRepository mensualidadRepository;
    private final CorreoMensualidadService correoMensualidadService;
    private final CobroService cobroService;

    /**
     * Método principal que ejecutará el Scheduler.
     *
     * Revisa las inscripciones confirmadas y: 1. Marca como vencidas las
     * mensualidades que no fueron pagadas. 2. Genera la mensualidad
     * correspondiente cuando llega su fecha.
     */
    @Transactional
    public void procesarMensualidades() {

        List<Inscripcion> inscripciones
                = inscripcionRepository.findByEstado("Confirmada");

        for (Inscripcion inscripcion : inscripciones) {

            marcarMensualidadesVencidas(inscripcion);

            generarMensualidadActual(inscripcion);
        }
    }

    /**
     * Marca como Vencidas las mensualidades cuya fecha de vencimiento ya pasó y
     * que todavía están Pendientes.
     */
    private void marcarMensualidadesVencidas(
            Inscripcion inscripcion) {

        List<Mensualidad> mensualidades
                = mensualidadRepository.findByInscripcionOrderByPeriodoAsc(
                        inscripcion);

        LocalDate hoy = LocalDate.now();

        for (Mensualidad mensualidad : mensualidades) {

            if (mensualidad.getEstado()
                    == Mensualidad.EstadoMensualidad.Pendiente
                    && mensualidad.getFechaVencimiento().isBefore(hoy)) {

                mensualidad.setEstado(
                        Mensualidad.EstadoMensualidad.Vencida);

                mensualidadRepository.save(mensualidad);
            }
        }
    }

    /**
     * Genera la mensualidad correspondiente al mes actual solamente cuando
     * llega o pasa el día de pago.
     */
    private void generarMensualidadActual(
            Inscripcion inscripcion) {

        LocalDate hoy = LocalDate.now();
        LocalDate fechaInscripcion = inscripcion.getFechaInscripcion();

        int diaPago = fechaInscripcion.getDayOfMonth();

        YearMonth mesActual = YearMonth.from(hoy);

        int ultimoDiaDelMes = mesActual.lengthOfMonth();

        if (diaPago > ultimoDiaDelMes) {
            diaPago = ultimoDiaDelMes;
        }

        LocalDate fechaVencimiento = mesActual.atDay(diaPago);

        /*
         * Todavía no ha llegado el día de pago de este mes.
         */
        if (hoy.isBefore(fechaVencimiento)) {
            return;
        }

        String periodo = mesActual.toString();

        if (!mensualidadRepository
                .existsByInscripcionAndPeriodo(inscripcion, periodo)) {

            Mensualidad mensualidad
                    = crearMensualidad(
                            inscripcion,
                            mesActual,
                            fechaVencimiento);

            mensualidadRepository.save(mensualidad);

            enviarCorreoNuevaMensualidad(mensualidad);
        }
    }

    /**
     * Construye una nueva mensualidad.
     */
    private Mensualidad crearMensualidad(
            Inscripcion inscripcion,
            YearMonth periodo,
            LocalDate fechaVencimiento) {

        Mensualidad mensualidad = new Mensualidad();

        mensualidad.setInscripcion(inscripcion);

        mensualidad.setPeriodo(periodo.toString());

        mensualidad.setFechaVencimiento(fechaVencimiento);

        mensualidad.setMonto(
                inscripcion.getCurso().getPrecioMensual());

        mensualidad.setEstado(
                Mensualidad.EstadoMensualidad.Pendiente);

        return mensualidad;
    }

    @Transactional(readOnly = true)
    public List<Mensualidad> obtenerMensualidades(Mensualidad.EstadoMensualidad estado, Integer idEstudiante) {
        if (idEstudiante != null && estado != null) {
            return mensualidadRepository.findByInscripcionEstudianteIdEstudianteAndEstadoOrderByFechaVencimientoDesc(idEstudiante, estado);
        } else if (estado != null && idEstudiante == null) {
            return mensualidadRepository.findByEstadoOrderByFechaVencimientoDesc(estado);
        } else if (idEstudiante != null && estado == null) {
            return mensualidadRepository.findByInscripcionEstudianteIdEstudianteOrderByFechaVencimientoDesc(idEstudiante);
        } else {
            return mensualidadRepository.findAllByOrderByFechaVencimientoDesc();
        }
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

    private void enviarCorreoNuevaMensualidad(Mensualidad mensualidad) {

        Estudiante estudiante = mensualidad
                .getInscripcion()
                .getEstudiante();

        String correo = obtenerCorreoNotificacion(estudiante);

        if (correo == null) {
            return;
        }

        String asunto = "Recordatorio Pago - Centro de Artes Taru";

        String contenido = """
            <html>
                <body>
                    <h2>Nueva mensualidad generada</h2>

                    <p>Hola %s %s,</p>

                    <p>
                        Se le recuerda el pago de la mensualidad
                        correspondiente al período <strong>%s</strong>.
                    </p>

                    <p>
                        <strong>Monto:</strong> ₡%s
                    </p>

                    <p>
                        Por favor, recuerde realizar el pago
                        a tiempo.
                    </p>

                    <p>
                        Centro de Artes Taru
                    </p>
                </body>
            </html>
            """.formatted(
                estudiante.getNombre(),
                estudiante.getApellido(),
                mensualidad.getPeriodo(),
                mensualidad.getMonto()
        );

        try {
            correoMensualidadService.enviarCorreoHtml(
                    correo,
                    asunto,
                    contenido
            );

            Cobro cobro = new Cobro();

            cobro.setMensualidad(mensualidad);
            cobro.setDestinatario(correo);
            cobro.setEstado("ENVIADO");

            cobroService.registrar(cobro);

        } catch (MessagingException e) {
            System.err.println(
                    "No se pudo enviar el correo de mensualidad: "
                    + e.getMessage()
            );
            // Registrar el intento fallido
            Cobro cobro = new Cobro();

            cobro.setMensualidad(mensualidad);
            cobro.setDestinatario(correo);
            cobro.setEstado("ERROR");

            cobroService.registrar(cobro);
        }
    }

}
