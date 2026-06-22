package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FaseSectorRequest {

    @NotNull
    private Long sectorId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;
}
