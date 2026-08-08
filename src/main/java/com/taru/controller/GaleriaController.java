package com.taru.controller;

import com.taru.domain.Galeria;
import com.taru.service.GaleriaService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/galeria")
public class GaleriaController {

    private final GaleriaService galeriaService;

    public GaleriaController(GaleriaService galeriaService) {
        this.galeriaService = galeriaService;
    }

    @GetMapping
    public String publico(Model model) {
        model.addAttribute("imagenes", galeriaService.getImagenes(true));
        return "/galeria/publico";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var imagenes = galeriaService.getImagenes(false);
        model.addAttribute("imagenes", imagenes);
        model.addAttribute("totalImagenes", imagenes.size());
        if (!model.containsAttribute("galeria")) {
            model.addAttribute("galeria", new Galeria());
        }
        return "/galeria/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Galeria galeria,
            @RequestParam(required = false) MultipartFile fotoFile,
            RedirectAttributes redirectAttributes) {
        galeriaService.save(galeria, fotoFile);
        redirectAttributes.addFlashAttribute("todoOk", "La imagen se guardó correctamente.");
        return "redirect:/galeria/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idGaleria, RedirectAttributes redirectAttributes) {
        try {
            galeriaService.delete(idGaleria);
            redirectAttributes.addFlashAttribute("todoOk", "La imagen se eliminó correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "La imagen no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/galeria/listado";
    }

    @GetMapping("/modifica/{idGaleria}")
    public String modificar(@PathVariable Integer idGaleria, Model model, RedirectAttributes redirectAttributes) {
        Optional<Galeria> galeriaOpt = galeriaService.getImagen(idGaleria);
        if (galeriaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La imagen no existe.");
            return "redirect:/galeria/listado";
        }
        model.addAttribute("galeria", galeriaOpt.get());
        return "/galeria/modifica";
    }
}