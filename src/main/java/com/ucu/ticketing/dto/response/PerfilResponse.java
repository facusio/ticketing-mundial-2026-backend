package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PerfilResponse {
    private Long id;
    private String mail;
    private String paisDoc;
    private String tipoDoc;
    private String numeroDoc;
    private String paisDir;
    private String localidad;
    private String calle;
    private String numeroDir;
    private String codigoPostal;
    private String rol;
    private LocalDate fechaRegistro;
    private String estadoVerificacion;
    private List<TelefonoDto> telefonos;

    @Data
    @Builder
    public static class TelefonoDto {
        private Long id;
        private String numero;
    }
}
