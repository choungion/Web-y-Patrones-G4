package com.taru.controller;

import com.taru.domain.Ausencia;
import com.taru.service.AusenciaService;
import com.taru.service.CursoService;
import com.taru.service.EstudianteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ausencia")
public class AusenciaController {

    private final AusenciaService ausenciaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;

    public AusenciaController(
            AusenciaService ausenciaService,
            EstudianteService estudianteService,
            CursoService cursoService) {

        this.ausenciaService = ausenciaService;
        this.estudianteService = estudianteService;
        this.cursoService = cursoService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("ausencia", new Ausencia());
        model.addAttribute("estudiantes",
                estudianteService.getEstudiantesEnCurso());
        model.addAttribute("cursos",
                cursoService.getCursos(true));

        return "ausencia/registro";
    }

    @PostMapping("/guardar")
    public String guardar(Ausencia ausencia) {

        ausenciaService.save(ausencia);

        return "redirect:/ausencia/historial";
    }

    @GetMapping("/historial")
    public String historial(Model model) {

        model.addAttribute(
                "ausencias",
                ausenciaService.getAusencias()
        );

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
