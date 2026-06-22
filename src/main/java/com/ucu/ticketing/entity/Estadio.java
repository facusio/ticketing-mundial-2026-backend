package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estadio", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Estadio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminPais adminPais;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "pais", nullable = false, length = 100)
    private String pais;

    @Column(name = "ciudad", nullable = false, length = 150)
    private String ciudad;
}
