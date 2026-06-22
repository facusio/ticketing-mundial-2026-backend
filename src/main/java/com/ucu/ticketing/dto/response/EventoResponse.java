package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventoResponse {
    private Long id;
    private LocalDateTime fechaHora;
    private String equipoLocal;
    private String equipoVisitante;
    private EstadioDto estadio;
    private FaseDto fase;

    @Data
    @Builder
    public static class EstadioDto {
        private Long id;
        private String nombre;
        private String ciudad;
        private String pais;
    }

    @Data
    @Builder
    public static class FaseDto {
        private Long id;
        private String nombre;
        private Integer orden;
    }
}
