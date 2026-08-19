package com.taru.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(unique = true, nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String username;

    @Column(nullable = false, length = 512)
    @NotNull
    @Size(max = 512)
    private String password;

    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String apellidos;

    @Column(unique = true, nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String correo;

    @Column(length = 25)
    @Size(max = 25)
    private String telefono;

    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    private boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();

    // Referencia opcional al estudiante asociado a esta cuenta
    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
}