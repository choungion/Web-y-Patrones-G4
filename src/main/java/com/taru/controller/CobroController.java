
package com.taru.controller;

import com.taru.service.CobroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cobro")
@RequiredArgsConstructor
public class CobroController {
    private final CobroService cobroService;

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("cobros", cobroService.obtenerCobros());

        return "/cobro/listado";
    }
}
