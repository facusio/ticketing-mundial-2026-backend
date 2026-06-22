package com.ucu.ticketing.controller;

import com.ucu.ticketing.dto.request.DispositivoRequest;
import com.ucu.ticketing.dto.request.ValidacionRequest;
import com.ucu.ticketing.dto.response.ValidacionResponse;
import com.ucu.ticketing.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionario")
@RequiredArgsConstructor
@Tag(name = "Funcionario", description = "Validación de entradas en puerta de estadio")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @PostMapping("/dispositivos/registrar")
    @Operation(summary = "Registrar dispositivo (navegador) del funcionario")
    public ResponseEntity<Void> registrarDispositivo(@Valid @RequestBody DispositivoRequest req,
                                                      Authentication auth) {
        funcionarioService.registrarDispositivo(funcionarioId(auth), req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sectores-asignados")
    @Operation(summary = "Sectores donde el funcionario puede validar entradas")
    public ResponseEntity<List<Map<String, Object>>> getSectoresAsignados(Authentication auth) {
        return ResponseEntity.ok(funcionarioService.getSectoresAsignados(funcionarioId(auth)));
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar entrada por código QR")
    public ResponseEntity<ValidacionResponse> validar(@Valid @RequestBody ValidacionRequest req,
                                                       Authentication auth) {
        return ResponseEntity.ok(funcionarioService.validar(funcionarioId(auth), req));
    }

    @GetMapping("/validaciones")
    @Operation(summary = "Historial de validaciones del funcionario")
    public ResponseEntity<List<ValidacionResponse>> getValidaciones(Authentication auth) {
        return ResponseEntity.ok(funcionarioService.getValidaciones(funcionarioId(auth)));
    }

    private Long funcionarioId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
