/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taru.controller;

import com.taru.service.PagoService;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/pago/guardar")
    public String guardarPago(
            @RequestParam Integer idMensualidad,
            @RequestParam LocalDate fechaPago,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String observaciones,
            @RequestParam MultipartFile reciboFile) throws IOException {

        pagoService.registrarPago(
                idMensualidad,
                fechaPago,
                metodoPago,
                observaciones,
                reciboFile
        );

        return "redirect:/mensualidad/listado";
    }
}
