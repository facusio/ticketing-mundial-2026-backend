package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "funcionario", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Funcionario {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "numero_legajo", nullable = false, unique = true, length = 50)
    private String numeroLegajo;
}
