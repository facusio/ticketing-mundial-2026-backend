package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "admin_pais", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminPais {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "pais_jurisdiccion", nullable = false, length = 100)
    private String paisJurisdiccion;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;
}
