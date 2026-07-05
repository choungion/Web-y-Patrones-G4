package com.taru.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "estudiante")
public class Estudiante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstudiante;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String apellido;

    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(length = 255)
    private String direccion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaFoto;

    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_encargado")
    private Encargado encargado;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Curso curso;
}
