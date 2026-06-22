package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PrecioSectorResponse {
    private Long sectorId;
    private String codigoSector;
    private Integer capacidadMaxima;
    private BigDecimal precio;
}
