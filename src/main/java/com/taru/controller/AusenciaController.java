package com.taru.controller;

import com.taru.domain.Ausencia;
import com.taru.domain.Curso;
import com.taru.domain.Estudiante;
import com.taru.service.AusenciaService;
import com.taru.service.CursoService;
import com.taru.service.EstudianteService;
import com.taru.service.InscripcionService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ausencia")
public class AusenciaController {

    private final AusenciaService ausenciaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    private final InscripcionService inscripcionService;

    public AusenciaController(
            AusenciaService ausenciaService,
            EstudianteService estudianteService,
            CursoService cursoService,
            InscripcionService inscripcionService) {

        this.ausenciaService = ausenciaService;
        this.estudianteService = estudianteService;
        this.cursoService = cursoService;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {

        Ausencia ausencia = new Ausencia();
        Estudiante estudianteLogueado = obtenerEstudianteDeSesion(session);

        if (estudianteLogueado != null) {
            List<Curso> cursosEstudiante = inscripcionService.getCursosInscritos(estudianteLogueado.getIdEstudiante());

            ausencia.setEstudiante(estudianteLogueado);
            if (cursosEstudiante.size() == 1) {
                ausencia.setCurso(cursosEstudiante.get(0));
            }

            model.addAttribute("cursos", cursosEstudiante);
        } else {
            model.addAttribute("estudiantes",
                    estudianteService.getEstudiantesEnCurso());
            model.addAttribute("cursos",
                    cursoService.getCursos(true));
        }

        model.addAttribute("ausencia", ausencia);
        model.addAttribute("estudianteLogueado", estudianteLogueado);

        return "ausencia/registro";
    }

    @PostMapping("/guardar")
    public String guardar(
            Ausencia ausencia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Estudiante estudianteLogueado = obtenerEstudianteDeSesion(session);

        if (estudianteLogueado != null) {
            ausencia.setEstudiante(estudianteLogueado);

            List<Curso> cursosPermitidos = inscripcionService.getCursosInscritos(estudianteLogueado.getIdEstudiante());
            Curso cursoEnviado = ausencia.getCurso();

            boolean cursoValido = cursoEnviado != null
                    && cursoEnviado.getIdCurso() != null
                    && cursosPermitidos.stream()
                            .anyMatch(c -> c.getIdCurso().equals(cursoEnviado.getIdCurso()));

            if (!cursoValido) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Solo puedes notificar una ausencia en un curso en el que estas inscrito."
                );
                return "redirect:/ausencia/nuevo";
            }
        }

        if (ausencia.getEstudiante() == null || ausencia.getCurso() == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo determinar el estudiante o el curso para la ausencia."
            );
            return "redirect:/ausencia/nuevo";
        }

        ausenciaService.save(ausencia);

        return "redirect:/ausencia/historial";
    }

    private Estudiante obtenerEstudianteDeSesion(HttpSession session) {
        Integer idEstudianteSesion = (Integer) session.getAttribute("usuarioIdEstudiante");
        if (idEstudianteSesion == null) {
            return null;
        }
        Optional<Estudiante> estudianteOpt = estudianteService.getEstudiante(idEstudianteSesion);
        return estudianteOpt.orElse(null);
    }

    @GetMapping("/historial")
    public String historial(Model model, HttpSession session) {

        Estudiante estudianteLogueado = obtenerEstudianteDeSesion(session);

        List<com.taru.domain.Ausencia> ausencias = estudianteLogueado != null
                ? ausenciaService.getAusenciasPorEstudiante(estudianteLogueado.getIdEstudiante())
                : ausenciaService.getAusencias();

        model.addAttribute("ausencias", ausencias);

        return "ausencia/historial";
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute(
                "ausencias",
                ausenciaService.getAusencias()
        );

        return "ausencia/historial";
    }

}