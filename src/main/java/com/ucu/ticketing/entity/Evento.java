package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadio_id", nullable = false)
    private Estadio estadio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminPais admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id", nullable = false)
    private Fase fase;

    @Column(name = "equipo_local", nullable = false, length = 100)
    private String equipoLocal;

    @Column(name = "equipo_visitante", nullable = false, length = 100)
    private String equipoVisitante;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
