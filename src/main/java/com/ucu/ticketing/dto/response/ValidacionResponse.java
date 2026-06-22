package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ValidacionResponse {
    private String mensaje;
    private Long entradaId;
    private PropietarioDto propietario;
    private EventoDto evento;
    private SectorDto sector;
    private LocalDateTime fechaValidacion;

    @Data
    @Builder
    public static class PropietarioDto {
        private Long id;
        private String mail;
        private String numeroDoc;
    }

    @Data
    @Builder
    public static class EventoDto {
        private Long id;
        private String equipoLocal;
        private String equipoVisitante;
        private LocalDateTime fechaHora;
    }

    @Data
    @Builder
    public static class SectorDto {
        private Long id;
        private String codigo;
        private String estadioNombre;
    }
}
