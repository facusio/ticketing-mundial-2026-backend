package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DispositivoRequest {

    @NotBlank
    @Size(max = 100)
    private String deviceUid;
}
