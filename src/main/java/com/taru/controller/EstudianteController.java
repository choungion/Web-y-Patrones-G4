package com.taru.controller;

import com.taru.domain.Estudiante;
import com.taru.service.CursoService;
import com.taru.service.EncargadoService;
import com.taru.service.EstudianteService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    private final EncargadoService encargadoService;

    public EstudianteController(EstudianteService estudianteService, CursoService cursoService, EncargadoService encargadoService) {
        this.estudianteService = estudianteService;
        this.cursoService = cursoService;
        this.encargadoService = encargadoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var estudiantes = estudianteService.getEstudiantes(false);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("totalEstudiantes", estudiantes.size());
        model.addAttribute("activos", estudiantes.stream().filter(Estudiante::isActivo).count());
        model.addAttribute("inactivos", estudiantes.stream().filter(e -> !e.isActivo()).count());
        model.addAttribute("cursos", cursoService.getCursos(true));
        model.addAttribute("encargados", encargadoService.getEncargados());
        if (!model.containsAttribute("estudiante")) {
            model.addAttribute("estudiante", new Estudiante());
        }
        return "/estudiante/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Estudiante estudiante, @RequestParam(required = false) MultipartFile fotoFile, RedirectAttributes redirectAttributes) {
        estudianteService.save(estudiante, fotoFile);
        redirectAttributes.addFlashAttribute("todoOk", "El estudiante se registro correctamente.");
        return "redirect:/estudiante/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idEstudiante, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "El registro ha sido eliminado permanentemente.";
        try {
            estudianteService.delete(idEstudiante);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "El estudiante no existe.";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        } catch (Exception e) {
            titulo = "error";
            detalle = "Se produjo un error inesperado al intentar eliminar el estudiante.";
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/estudiante/listado";
    }

    @GetMapping("/modificar/{idEstudiante}")
    public String modificar(@PathVariable("idEstudiante") Integer idEstudiante, Model model, RedirectAttributes redirectAttributes) {
        Optional<Estudiante> estudianteOpt = estudianteService.getEstudiante(idEstudiante);
        if (estudianteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El estudiante no existe.");
            return "redirect:/estudiante/listado";
        }
        model.addAttribute("estudiante", estudianteOpt.get());
        model.addAttribute("cursos", cursoService.getCursos(true));
        model.addAttribute("encargados", encargadoService.getEncargados());
        return "/estudiante/modifica";
    }
    
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public String manejarParametroInvalido(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "No se pudo completar la accion: falta o es invalido un dato requerido. Intente nuevamente.");
        return "redirect:/estudiante/listado";
    }
}
