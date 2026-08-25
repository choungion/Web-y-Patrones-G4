/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taru.controller;

import com.taru.domain.Mensualidad;
import com.taru.domain.Pago;
import com.taru.repository.EstudianteRepository;
import com.taru.repository.PagoRepository;
import com.taru.service.MensualidadService;
import com.taru.service.PagoService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mensualidad")
@RequiredArgsConstructor
public class mensualidadController {

    private final MensualidadService mensualidadService;
    private final EstudianteRepository estudianteRepository;
    private final PagoRepository pagoRepository;
    private final PagoService pagoService;

    @GetMapping("/mensualidades/generar")
    public String generarMensualidades() {

        mensualidadService.procesarMensualidades();

        return "redirect:/";
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam(required = false) Mensualidad.EstadoMensualidad estado,
            @RequestParam(required = false) Integer idEstudiante,
            Model model, HttpSession session) {

        Integer usuarioIdEstudiante
                = (Integer) session.getAttribute("usuarioIdEstudiante");

        List<Mensualidad> mensualidades;
        if (usuarioIdEstudiante != null) {

            // Estudiante: solamente sus mensualidades
            mensualidades = mensualidadService.obtenerMensualidades(
                    estado,
                    usuarioIdEstudiante
            );
        } else {

            // Administradora: puede filtrar libremente
            mensualidades = mensualidadService.obtenerMensualidades(
                    estado,
                    idEstudiante
            );
        }

        Map<Integer, Pago> pagos = new HashMap<>();
        for (Mensualidad mensualidad : mensualidades) {
            pagoService
                    .obtenerPagoPorMensualidad(mensualidad.getIdMensualidad())
                    .ifPresent(pago
                            -> pagos.put(mensualidad.getIdMensualidad(), pago)
                    );
        }

        model.addAttribute("pagos", pagos);
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

    @GetMapping("/procesar")
    public String procesarMensualidades() {

        mensualidadService.procesarMensualidades();

        return "Proceso ejecutado";
    }
//traer el pago que pertenece a la mensualidad pagada para mostrar los datos

    @GetMapping("/pago/{idMensualidad}")
    @ResponseBody
    public Pago obtenerPago(@PathVariable Integer idMensualidad
    ) {

        return pagoRepository
                .findByMensualidad_IdMensualidad(idMensualidad)
                .orElse(null);
    }
}
