package com.taru.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "galeria")
public class Galeria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGaleria;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String titulo;

    @Column(length = 255)
    @Size(max = 255)
    private String descripcion;

    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    private boolean activo;
}