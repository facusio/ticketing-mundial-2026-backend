package com.ucu.ticketing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferenciaRequest {

    @NotNull
    private Long entradaId;

    @NotBlank
    @Email
    private String destinoMail;
}
