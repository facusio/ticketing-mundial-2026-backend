package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadioResponse {
    private Long id;
    private String nombre;
    private String ciudad;
    private String pais;
}
