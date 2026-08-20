
package com.taru.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "cobro")
public class Cobro implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCobro;

    @ManyToOne
    @JoinColumn(name = "id_mensualidad", nullable = false)
    private Mensualidad mensualidad;

    @Column(nullable = false, length = 100)
    private String destinatario;


    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(nullable = false, length = 20)
    private String estado;
}
