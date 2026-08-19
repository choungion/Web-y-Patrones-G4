package com.taru.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "nosotros")
public class Nosotros implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNosotros;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String parrafo1;

    @Column(columnDefinition = "TEXT")
    private String parrafo2;

    @Column(columnDefinition = "TEXT")
    private String mision;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    private boolean activo;
}