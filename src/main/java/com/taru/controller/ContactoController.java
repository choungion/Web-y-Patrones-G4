package com.taru.controller;

import com.taru.domain.Contacto;
import com.taru.service.ContactoService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD de la informacion de contacto del centro (telefono, dirección,
 * correo, redes sociales, horario) que se muestra en el sitio publico.
 * Solo accesible para la administradora.
 */
@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var registros = contactoService.getRegistros();
        model.addAttribute("registros", registros);
        model.addAttribute("totalRegistros", registros.size());
        if (!model.containsAttribute("contacto")) {
            model.addAttribute("contacto", new Contacto());
        }
        return "/contacto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Contacto contacto, RedirectAttributes redirectAttributes) {
        contactoService.save(contacto);
        redirectAttributes.addFlashAttribute("todoOk", "La información de contacto se guardó correctamente.");
        return "redirect:/contacto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idContacto, RedirectAttributes redirectAttributes) {
        try {
            contactoService.delete(idContacto);
            redirectAttributes.addFlashAttribute("todoOk", "El registro se eliminó correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "El registro no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contacto/listado";
    }

    @GetMapping("/modificar/{idContacto}")
    public String modificar(@PathVariable Integer idContacto, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("contacto", contactoService.getRegistro(idContacto));
            return "/contacto/modifica";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "El registro no existe.");
            return "redirect:/contacto/listado";
        }
    }
}