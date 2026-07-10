
package com.taru.controller;

import com.taru.domain.Asistencia;
import com.taru.domain.Estudiante;
import com.taru.service.AsistenciaService;
import com.taru.service.CursoService;
import com.taru.service.EstudianteService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;

    public AsistenciaController(
            AsistenciaService asistenciaService,
            EstudianteService estudianteService,
            CursoService cursoService
    ) {
        this.asistenciaService = asistenciaService;
        this.estudianteService = estudianteService;
        this.cursoService = cursoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute(
                "asistencias",
                asistenciaService.getAsistencias()
        );
        model.addAttribute(
                "estudiantes",
                estudianteService.getEstudiantes(false)
        );
        model.addAttribute(
                "cursos",
                cursoService.getCursos(false)
        );

        return "asistencia/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Asistencia asistencia = new Asistencia();
        asistencia.setFecha(LocalDate.now());

        model.addAttribute("asistencia", asistencia);
        model.addAttribute(
                "estudiantes",
                estudianteService.getEstudiantes(true)
        );
        model.addAttribute(
                "cursos",
                cursoService.getCursos(true)
        );

        return "asistencia/registro";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Asistencia asistencia,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute(
                    "estudiantes",
                    estudianteService.getEstudiantes(true)
            );
            model.addAttribute(
                    "cursos",
                    cursoService.getCursos(true)
            );

            return "asistencia/registro";
        }

        boolean existe = asistenciaService.existeAsistencia(
                asistencia.getEstudiante(),
                asistencia.getFecha()
        );

        if (existe && asistencia.getIdAsistencia() == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ya existe una asistencia para ese estudiante en esa fecha."
            );

            return "redirect:/asistencia/nuevo";
        }

        asistenciaService.save(asistencia);

        redirectAttributes.addFlashAttribute(
                "todoOK",
                "La asistencia se guardó correctamente."
        );

        return "redirect:/asistencia/listado";
    }

    @GetMapping("/historial/{idEstudiante}")
    public String historial(
            @PathVariable Integer idEstudiante,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Estudiante> estudianteOpt =
                estudianteService.getEstudiante(idEstudiante);

        if (estudianteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El estudiante no existe."
            );

            return "redirect:/asistencia/listado";
        }

        Estudiante estudiante = estudianteOpt.get();

        model.addAttribute("estudiante", estudiante);
        model.addAttribute(
                "asistencias",
                asistenciaService.getAsistenciasPorEstudiante(estudiante)
        );

        return "asistencia/historial";
    }

    @GetMapping("/modificar/{idAsistencia}")
    public String modificar(
            @PathVariable Integer idAsistencia,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Asistencia> asistenciaOpt =
                asistenciaService.getAsistencia(idAsistencia);

        if (asistenciaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "La asistencia no existe."
            );

            return "redirect:/asistencia/listado";
        }

        model.addAttribute("asistencia", asistenciaOpt.get());
        model.addAttribute(
                "estudiantes",
                estudianteService.getEstudiantes(true)
        );
        model.addAttribute(
                "cursos",
                cursoService.getCursos(true)
        );

        return "asistencia/registro";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idAsistencia,
            RedirectAttributes redirectAttributes
    ) {
        try {
            asistenciaService.delete(idAsistencia);

            redirectAttributes.addFlashAttribute(
                    "todoOK",
                    "La asistencia se eliminó correctamente."
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/asistencia/listado";
    }
}

