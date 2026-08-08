package com.taru.controller;

import com.taru.domain.Rol;
import com.taru.service.RolService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rol")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var roles = rolService.getRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("totalRoles", roles.size());
        if (!model.containsAttribute("rol")) {
            model.addAttribute("rol", new Rol());
        }
        return "/rol/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Rol rol, RedirectAttributes redirectAttributes) {
        rolService.save(rol);
        redirectAttributes.addFlashAttribute("todoOk", "El rol se guardó correctamente.");
        return "redirect:/rol/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idRol, RedirectAttributes redirectAttributes) {
        try {
            rolService.delete(idRol);
            redirectAttributes.addFlashAttribute("todoOk", "El rol se eliminó correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "El rol no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el rol, tiene usuarios o rutas asociadas.");
        }
        return "redirect:/rol/listado";
    }

    @GetMapping("/modificar/{idRol}")
    public String modificar(@PathVariable Integer idRol, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("rol", rolService.getRol(idRol));
            return "/rol/modifica";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "El rol no existe.");
            return "redirect:/rol/listado";
        }
    }
}