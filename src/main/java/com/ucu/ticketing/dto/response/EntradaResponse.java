package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class EntradaResponse {
    private Long id;
    private String estado;
    private BigDecimal precio;
    private Short transferenciasRealizadas;
    private SectorDto sector;
    private EventoDto evento;

    @Data
    @Builder
    public static class SectorDto {
        private Long id;
        private String codigo;
    }

    @Data
    @Builder
    public static class EventoDto {
        private Long id;
        private LocalDateTime fechaHora;
        private String equipoLocal;
        private String equipoVisitante;
        private String estadioNombre;
    }
}
