/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taru.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author diana
 */
@Service
public class CorreoMensualidadService {
    private final JavaMailSender mailSender;

    public CorreoMensualidadService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) throws MessagingException {

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper correo = new MimeMessageHelper(mensaje, true);

        correo.setTo(para);
        correo.setSubject(asunto);
        correo.setText(contenido, true);
        mailSender.send(mensaje);
    }
}
