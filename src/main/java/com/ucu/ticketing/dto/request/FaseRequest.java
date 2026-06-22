package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FaseRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Min(1)
    private Integer orden;
}
