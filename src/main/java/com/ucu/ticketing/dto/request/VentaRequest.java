package com.ucu.ticketing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequest {

    @NotNull
    private Long eventoId;

    @NotEmpty
    @Size(max = 5)
    @Valid
    private List<ItemVentaRequest> items;

    @Data
    public static class ItemVentaRequest {
        @NotNull
        private Long sectorId;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer cantidad;
    }
}
