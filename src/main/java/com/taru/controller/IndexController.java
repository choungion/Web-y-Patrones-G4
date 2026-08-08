package com.taru.controller;

import com.taru.domain.Curso;
import com.taru.service.CursoService;
import com.taru.service.GaleriaService;
import com.taru.service.NosotrosService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final CursoService cursoService;
    private final NosotrosService nosotrosService;
    private final GaleriaService galeriaService;

    public IndexController(CursoService cursoService, NosotrosService nosotrosService, GaleriaService galeriaService) {
        this.cursoService = cursoService;
        this.nosotrosService = nosotrosService;
        this.galeriaService = galeriaService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Curso> cursos = cursoService.getCursos(true);
        model.addAttribute("cursos", cursos);

        // HU-17 / CRUD de Nosotros: se muestra el registro activo (si existe)
        nosotrosService.getActivo().ifPresent(n -> model.addAttribute("nosotrosInfo", n));

        // Vista previa de la galeria en el inicio (maximo 4 imagenes)
        var imagenes = galeriaService.getImagenes(true);
        model.addAttribute("imagenesGaleria", imagenes.size() > 4 ? imagenes.subList(0, 4) : imagenes);

        return "index";
    }
}