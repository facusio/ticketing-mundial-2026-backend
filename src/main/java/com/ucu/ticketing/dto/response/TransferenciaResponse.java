package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransferenciaResponse {
    private Long id;
    private Long entradaId;
    private String estado;
    private LocalDateTime fechaHora;
    private UsuarioDto origen;
    private UsuarioDto destino;
    private EventoDto evento;

    @Data
    @Builder
    public static class UsuarioDto {
        private Long id;
        private String mail;
    }

    @Data
    @Builder
    public static class EventoDto {
        private Long id;
        private String equipoLocal;
        private String equipoVisitante;
    }
}
