package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidacionRequest {

    @NotBlank
    private String codigoQr;

    @NotBlank
    private String deviceUid;
}
