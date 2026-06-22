package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "funcionario_sector", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FuncionarioSector {

    @EmbeddedId
    private FuncionarioSectorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("funcionarioId")
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sectorId")
    @JoinColumn(name = "sector_id")
    private Sector sector;
}
