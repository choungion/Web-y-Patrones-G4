/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taru.controller;

import com.taru.domain.Mensualidad;
import com.taru.repository.EstudianteRepository;
import com.taru.service.MensualidadService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mensualidad")
@RequiredArgsConstructor
public class mensualidadController {

    private final MensualidadService mensualidadService;
    private final EstudianteRepository estudianteRepository;

    @GetMapping("/mensualidades/generar")
    public String generarMensualidades() {

        mensualidadService.procesarMensualidades();

        return "redirect:/";
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam(required = false) Mensualidad.EstadoMensualidad estado,
            @RequestParam(required = false) Integer idEstudiante,
            Model model) {
        List<Mensualidad> mensualidades
                = mensualidadService.obtenerMensualidades(estado, idEstudiante);
        model.addAttribute(
                "estudiantes",
                estudianteRepository.findAll()
        );
        model.addAttribute("mensualidades", mensualidades);

        // Totales por estado
        long pagadas = mensualidades.stream()
                .filter(m -> m.getEstado() == Mensualidad.EstadoMensualidad.Pagada)
                .count();

        long pendientes = mensualidades.stream()
                .filter(m -> m.getEstado() == Mensualidad.EstadoMensualidad.Pendiente)
                .count();

        long vencidas = mensualidades.stream()
                .filter(m -> m.getEstado() == Mensualidad.EstadoMensualidad.Vencida)
                .count();

        model.addAttribute("pagadas", pagadas);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("vencidas", vencidas);
        if (!model.containsAttribute("mensualidad")) {
            model.addAttribute("mensualidad", new Mensualidad());
        }
        return "/mensualidad/listado";
    }

 
}
