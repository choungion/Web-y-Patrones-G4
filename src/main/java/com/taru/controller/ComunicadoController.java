package com.taru.controller;

import com.taru.domain.Comunicado;
import com.taru.domain.Estudiante;
import com.taru.service.ComunicadoService;
import com.taru.service.EstudianteService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comunicado")
public class ComunicadoController {

    private final ComunicadoService comunicadoService;
    private final EstudianteService estudianteService;

    public ComunicadoController(
            ComunicadoService comunicadoService,
            EstudianteService estudianteService) {

        this.comunicadoService = comunicadoService;
        this.estudianteService = estudianteService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("comunicado", new Comunicado());
        
        model.addAttribute(
                "estudiantes",
                estudianteService.getEstudiantesEnCurso()
        );

        return "comunicado/registro";
    }

    @PostMapping("/guardar")
    public String guardar(
            Comunicado comunicado,
            RedirectAttributes redirectAttributes) {

        comunicadoService.save(comunicado);

        redirectAttributes.addFlashAttribute(
                "todoOK",
                "Comunicado enviado correctamente."
        );

        return "redirect:/comunicado/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute(
                "comunicados",
                comunicadoService.getComunicados()
        );

        return "comunicado/listado";
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public String comunicadosEstudiante(
            @PathVariable Integer idEstudiante,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Estudiante> estudianteOpt
                = estudianteService.getEstudiante(idEstudiante);

        if (estudianteOpt.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El estudiante no existe."
            );

            return "redirect:/";
        }

        Estudiante estudiante = estudianteOpt.get();

        model.addAttribute("estudiante", estudiante);

        model.addAttribute(
                "comunicados",
                comunicadoService
                        .getComunicadosPorEstudiante(estudiante)
        );

        return "comunicado/notificaciones";
    }
}
