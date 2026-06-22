package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VentaResponse {
    private Long id;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal montoTotal;
    private List<EntradaResumenDto> entradas;

    @Data
    @Builder
    public static class EntradaResumenDto {
        private Long id;
        private String estado;
        private BigDecimal precio;
        private String codigoSector;
    }
}
