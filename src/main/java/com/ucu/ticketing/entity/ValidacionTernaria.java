package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "validacion_ternaria", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidacionTernaria {

    @EmbeddedId
    private ValidacionTernariaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("funcionarioId")
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("entradaId")
    @JoinColumn(name = "entrada_id")
    private Entrada entrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_qr_id", nullable = false)
    private TokenQr tokenQr;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
