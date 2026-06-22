package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dispositivo", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "device_uid", nullable = false, unique = true, length = 100)
    private String deviceUid;
}
