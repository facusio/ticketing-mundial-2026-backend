package com.ucu.ticketing.controller;

import com.ucu.ticketing.dto.request.*;
import com.ucu.ticketing.dto.response.EstadioResponse;
import com.ucu.ticketing.dto.response.EventoResponse;
import com.ucu.ticketing.entity.Fase;
import com.ucu.ticketing.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin País", description = "Gestión de estadios, sectores y eventos")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/estadios")
    @Operation(summary = "Crear estadio")
    public ResponseEntity<EstadioResponse> crearEstadio(@Valid @RequestBody EstadioRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearEstadio(adminId(auth), req));
    }

    @GetMapping("/estadios")
    @Operation(summary = "Listar estadios del admin autenticado")
    public ResponseEntity<List<EstadioResponse>> getEstadios(Authentication auth) {
        return ResponseEntity.ok(adminService.getEstadios(adminId(auth)));
    }

    @PostMapping("/estadios/{id}/sectores")
    @Operation(summary = "Crear sector en un estadio")
    public ResponseEntity<Void> crearSector(@PathVariable Long id,
                                             @Valid @RequestBody SectorRequest req,
                                             Authentication auth) {
        adminService.crearSector(adminId(auth), id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/eventos")
    @Operation(summary = "Crear evento (valida no superposición de horarios)")
    public ResponseEntity<EventoResponse> crearEvento(@Valid @RequestBody EventoRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearEvento(adminId(auth), req));
    }

    @GetMapping("/eventos")
    @Operation(summary = "Listar eventos del admin autenticado")
    public ResponseEntity<List<EventoResponse>> getEventos(Authentication auth) {
        return ResponseEntity.ok(adminService.getEventos(adminId(auth)));
    }

    @PostMapping("/fases")
    @Operation(summary = "Crear fase del torneo")
    public ResponseEntity<Fase> crearFase(@Valid @RequestBody FaseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearFase(req));
    }

    @PostMapping("/fases/{id}/precios")
    @Operation(summary = "Definir precio de un sector para una fase")
    public ResponseEntity<Void> definirPrecio(@PathVariable Long id,
                                               @Valid @RequestBody FaseSectorRequest req) {
        adminService.definirPrecio(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reportes/ranking-eventos")
    @Operation(summary = "Ranking de eventos por entradas vendidas")
    public ResponseEntity<List<Map<String, Object>>> getRankingEventos() {
        return ResponseEntity.ok(adminService.getRankingEventos());
    }

    @GetMapping("/reportes/ranking-compradores")
    @Operation(summary = "Ranking de compradores por gasto total")
    public ResponseEntity<List<Map<String, Object>>> getRankingCompradores() {
        return ResponseEntity.ok(adminService.getRankingCompradores());
    }

    @GetMapping("/reportes/auditoria-funcionarios")
    @Operation(summary = "Auditoría de validaciones por funcionario")
    public ResponseEntity<List<Map<String, Object>>> getAuditoriaFuncionarios() {
        return ResponseEntity.ok(adminService.getAuditoriaFuncionarios());
    }

    private Long adminId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
