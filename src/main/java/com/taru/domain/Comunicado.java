package com.taru.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "comunicado")
public class Comunicado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComunicado;

    @Column(length = 150, nullable = false)
    private String titulo;

    @Column(length = 1000, nullable = false)
    private String mensaje;

    private LocalDateTime fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
}
