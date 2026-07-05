package com.taru.controller;

import com.taru.domain.Curso;
import com.taru.service.CursoService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final CursoService cursoService;

    public IndexController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Curso> cursos = cursoService.getCursos(true);
        model.addAttribute("cursos", cursos);
        return "index";
    }
}
