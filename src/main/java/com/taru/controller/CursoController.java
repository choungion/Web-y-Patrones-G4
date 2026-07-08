/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taru.controller;

import com.taru.domain.Curso;
import com.taru.service.CursoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {

        model.addAttribute("cursos", cursoService.getCursos(true));
        if (!model.containsAttribute("curso")) {
            model.addAttribute("curso", new Curso());
        }
        return "/cursos/servicios";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("cursos", cursoService.getCursos(false));
        if (!model.containsAttribute("curso")) {
            model.addAttribute("curso", new Curso());
        }
        return "/cursos/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Curso curso, @RequestParam(required = false) MultipartFile fotoFile, RedirectAttributes redirectAttributes) {
        cursoService.save(curso, fotoFile);
        redirectAttributes.addFlashAttribute("todoOk", "El curso se registro correctamente.");
        return "redirect:/cursos/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCurso, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "El registro ha sido eliminado permanentemente.";
        try {
            cursoService.delete(idCurso);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "El curso no existe.";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        } catch (Exception e) {
            titulo = "error";
            detalle = "Se produjo un error inesperado al intentar eliminar el curso.";
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/cursos/listado";
    }

    @GetMapping("/modifica/{idCurso}")
    public String modificar(@PathVariable("idCurso") Integer idCurso, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOpt = cursoService.getCurso(idCurso);
        if (cursoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El curso no existe.");
            return "redirect:/cursos/listado";
        }
        model.addAttribute("curso", cursoOpt.get());

        return "/cursos/modifica";
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public String manejarParametroInvalido(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "No se pudo completar la accion: falta o es invalido un dato requerido. Intente nuevamente.");
        return "redirect:/cursos/listado";
    }
}
