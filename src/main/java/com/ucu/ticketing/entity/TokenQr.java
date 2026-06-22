package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_qr", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenQr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", nullable = false)
    private Entrada entrada;

    @Column(name = "codigo", nullable = false, unique = true, length = 500)
    private String codigo;

    @Column(name = "generado_en", nullable = false)
    private LocalDateTime generadoEn;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
