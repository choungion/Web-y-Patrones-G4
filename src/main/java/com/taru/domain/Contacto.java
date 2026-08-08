package com.taru.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "contacto")
public class Contacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContacto;

    @Column(nullable = false, length = 25)
    @NotNull
    @Size(max = 25)
    private String telefono;

    @Column(nullable = false, length = 255)
    @NotNull
    @Size(max = 255)
    private String direccion;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String correo;

    @Column(length = 25)
    @Size(max = 25)
    private String whatsapp;

    @Column(length = 255)
    @Size(max = 255)
    private String facebook;

    @Column(length = 255)
    @Size(max = 255)
    private String instagram;

    @Column(length = 100)
    @Size(max = 100)
    private String horario;

    private boolean activo;
}