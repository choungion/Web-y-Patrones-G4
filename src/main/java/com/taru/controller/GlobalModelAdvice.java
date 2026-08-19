package com.taru.controller;

import com.taru.service.ContactoService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final ContactoService contactoService;

    public GlobalModelAdvice(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @ModelAttribute
    public void agregarContactoActivo(Model model) {
        contactoService.getActivo().ifPresent(contacto -> model.addAttribute("contactoActivo", contacto));
    }
}