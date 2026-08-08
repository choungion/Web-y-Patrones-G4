package com.taru.controller;

import com.taru.domain.Nosotros;
import com.taru.service.NosotrosService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nosotros")
public class NosotrosController {

    private final NosotrosService nosotrosService;

    public NosotrosController(NosotrosService nosotrosService) {
        this.nosotrosService = nosotrosService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var registros = nosotrosService.getRegistros();
        model.addAttribute("registros", registros);
        model.addAttribute("totalRegistros", registros.size());
        if (!model.containsAttribute("nosotros")) {
            model.addAttribute("nosotros", new Nosotros());
        }
        return "/nosotros/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Nosotros nosotros,
            @RequestParam(required = false) MultipartFile fotoFile,
            RedirectAttributes redirectAttributes) {
        nosotrosService.save(nosotros, fotoFile);
        redirectAttributes.addFlashAttribute("todoOk", "La información de Nosotros se guardó correctamente.");
        return "redirect:/nosotros/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idNosotros, RedirectAttributes redirectAttributes) {
        try {
            nosotrosService.delete(idNosotros);
            redirectAttributes.addFlashAttribute("todoOk", "El registro se eliminó correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "El registro no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nosotros/listado";
    }

    @GetMapping("/modifica/{idNosotros}")
    public String modificar(@PathVariable Integer idNosotros, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("nosotros", nosotrosService.getRegistro(idNosotros));
            return "/nosotros/modifica";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "El registro no existe.");
            return "redirect:/nosotros/listado";
        }
    }
}