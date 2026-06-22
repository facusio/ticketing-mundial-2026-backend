package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuario_general", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioGeneral {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "estado_verificacion", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EstadoVerificacion estadoVerificacion;

    public enum EstadoVerificacion {
        NO_VERIFICADO, PENDIENTE, VERIFICADO
    }
}
