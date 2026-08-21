package com.taru.controller;

import com.taru.domain.Curso;
import com.taru.domain.Encargado;
import com.taru.domain.Estudiante;
import com.taru.domain.Inscripcion;
import com.taru.service.CursoService;
import com.taru.service.EncargadoService;
import com.taru.service.EstudianteService;
import com.taru.service.InscripcionService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {

    private final CursoService cursoService;
    private final EstudianteService estudianteService;
    private final EncargadoService encargadoService;
    private final InscripcionService inscripcionService;

    public InscripcionController(CursoService cursoService, EstudianteService estudianteService,
            EncargadoService encargadoService, InscripcionService inscripcionService) {
        this.cursoService = cursoService;
        this.estudianteService = estudianteService;
        this.encargadoService = encargadoService;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("cursos", cursoService.getCursos(true));
        return "/inscripcion/listado";
    }

    @GetMapping("/formulario/{idCurso}")
    public String formulario(@PathVariable Integer idCurso, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOpt = cursoService.getCurso(idCurso);
        if (cursoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El curso no existe.");
            return "redirect:/inscripcion/listado";
        }
        model.addAttribute("curso", cursoOpt.get());
        if (!model.containsAttribute("estudiante")) {
            model.addAttribute("estudiante", new Estudiante());
        }
        return "/inscripcion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Estudiante estudiante, @RequestParam Integer idCurso,
            @RequestParam(required = false) String responsableNombre,
            @RequestParam(required = false) String responsableTelefono,
            RedirectAttributes redirectAttributes) {

        Optional<Curso> cursoOpt = cursoService.getCurso(idCurso);
        if (cursoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El curso no existe.");
            return "redirect:/inscripcion/listado";
        }
        Curso curso = cursoOpt.get();

        estudiante.setIdEstudiante(null);

        Encargado encargado = encargadoService.findOrCreate(responsableNombre, responsableTelefono, null);
        estudiante.setEncargado(encargado);
        estudiante.setCurso(curso);
        estudiante.setActivo(true);
        estudianteService.save(estudiante, null);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCurso(curso);
        inscripcionService.save(inscripcion);

        redirectAttributes.addFlashAttribute("todoOk",
                "Tu inscripcion fue registrada correctamente. Nos pondremos en contacto contigo pronto.");
        return "redirect:/inscripcion/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "/inscripcion/confirmacion";
    }
}
