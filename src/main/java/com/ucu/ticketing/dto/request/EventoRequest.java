package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventoRequest {

    @NotNull
    private Long estadioId;

    @NotNull
    private Long faseId;

    @NotNull
    private LocalDateTime fechaHora;

    @NotBlank
    @Size(max = 100)
    private String equipoLocal;

    @NotBlank
    @Size(max = 100)
    private String equipoVisitante;
}
