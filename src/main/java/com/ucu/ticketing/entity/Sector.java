package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sector", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadio_id", nullable = false)
    private Estadio estadio;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;
}
