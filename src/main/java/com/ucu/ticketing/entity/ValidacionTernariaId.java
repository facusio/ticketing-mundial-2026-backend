package com.ucu.ticketing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ValidacionTernariaId implements Serializable {

    @Column(name = "funcionario_id")
    private Long funcionarioId;

    @Column(name = "dispositivo_id")
    private Long dispositivoId;

    @Column(name = "entrada_id")
    private Long entradaId;
}
