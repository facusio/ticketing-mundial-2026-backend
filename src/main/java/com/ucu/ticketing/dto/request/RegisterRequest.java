package com.ucu.ticketing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    @Size(max = 255)
    private String mail;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String paisDoc;

    @NotBlank
    @Size(max = 50)
    private String tipoDoc;

    @NotBlank
    @Size(max = 100)
    private String numeroDoc;

    @NotBlank
    @Size(max = 100)
    private String paisDir;

    @NotBlank
    @Size(max = 150)
    private String localidad;

    @NotBlank
    @Size(max = 200)
    private String calle;

    @NotBlank
    @Size(max = 20)
    private String numeroDir;

    @NotBlank
    @Size(max = 20)
    private String codigoPostal;

    @NotEmpty
    @Valid
    private List<TelefonoRequest> telefonos;

    @Data
    public static class TelefonoRequest {
        @NotBlank
        @Size(max = 30)
        private String numero;
    }
}
