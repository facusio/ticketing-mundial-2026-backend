package com.ucu.ticketing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QrResponse {
    private String codigo;
    private LocalDateTime expiraEn;
    private Long entradaId;
}
