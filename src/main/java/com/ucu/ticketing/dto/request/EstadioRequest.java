package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EstadioRequest {

    @NotBlank
    @Size(max = 200)
    private String nombre;

    @NotBlank
    @Size(max = 150)
    private String ciudad;

    @NotBlank
    @Size(max = 100)
    private String pais;
}
