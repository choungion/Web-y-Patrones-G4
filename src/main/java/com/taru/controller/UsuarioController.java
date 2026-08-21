package com.taru.controller;

import com.taru.domain.Usuario;
import com.taru.service.EstudianteService;
import com.taru.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EstudianteService estudianteService;

    public UsuarioController(UsuarioService usuarioService, EstudianteService estudianteService) {
        this.usuarioService = usuarioService;
        this.estudianteService = estudianteService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("estudiantes", estudianteService.getEstudiantes(false));
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "/usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revise los datos del formulario, hay campos inválidos.");
            if (usuario.getIdUsuario() == null) {
                return "redirect:/usuario/listado";
            }
            return "redirect:/usuario/modificar/" + usuario.getIdUsuario();
        }
        try {
            usuarioService.save(usuario, imagenFile);
            redirectAttributes.addFlashAttribute("todoOk", "El usuario se guardó correctamente.");
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(idUsuario);
            redirectAttributes.addFlashAttribute("todoOk", "El usuario se eliminó correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el usuario, tiene datos asociados.");
        }
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no fue encontrado.");
            return "redirect:/usuario/listado";
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setPassword("");
        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiantes", estudianteService.getEstudiantes(false));
        return "/usuario/modifica";
    }
}