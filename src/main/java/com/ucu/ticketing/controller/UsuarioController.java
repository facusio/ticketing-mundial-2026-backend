package com.ucu.ticketing.controller;

import com.ucu.ticketing.dto.request.TransferenciaRequest;
import com.ucu.ticketing.dto.request.VentaRequest;
import com.ucu.ticketing.dto.response.*;
import com.ucu.ticketing.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario General", description = "Endpoints para compradores de entradas")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/perfil")
    @Operation(summary = "Perfil del usuario autenticado")
    public ResponseEntity<PerfilResponse> getPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.getPerfil(usuarioId(auth)));
    }

    @GetMapping("/eventos")
    @Operation(summary = "Listar próximos eventos, filtros opcionales por país y estadioId")
    public ResponseEntity<List<EventoResponse>> getEventos(
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) Long estadioId) {
        return ResponseEntity.ok(usuarioService.getEventos(pais, estadioId));
    }

    @GetMapping("/eventos/{id}/precios")
    @Operation(summary = "Precios por sector para un evento")
    public ResponseEntity<List<PrecioSectorResponse>> getPrecios(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getPreciosPorEvento(id));
    }

    @PostMapping("/ventas")
    @Operation(summary = "Crear venta (máximo 5 entradas)")
    public ResponseEntity<VentaResponse> crearVenta(@Valid @RequestBody VentaRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearVenta(usuarioId(auth), req));
    }

    @GetMapping("/ventas")
    @Operation(summary = "Historial de ventas del usuario")
    public ResponseEntity<List<VentaResponse>> getVentas(Authentication auth) {
        return ResponseEntity.ok(usuarioService.getVentas(usuarioId(auth)));
    }

    @GetMapping("/entradas")
    @Operation(summary = "Entradas donde el usuario es propietario actual")
    public ResponseEntity<List<EntradaResponse>> getEntradas(Authentication auth) {
        return ResponseEntity.ok(usuarioService.getEntradas(usuarioId(auth)));
    }

    @GetMapping("/entradas/{id}/qr")
    @Operation(summary = "QR activo de una entrada (se regenera cada 30 segundos)")
    public ResponseEntity<QrResponse> getQr(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(usuarioService.getQr(usuarioId(auth), id));
    }

    @PostMapping("/transferencias")
    @Operation(summary = "Iniciar transferencia de una entrada")
    public ResponseEntity<TransferenciaResponse> crearTransferencia(
            @Valid @RequestBody TransferenciaRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crearTransferencia(usuarioId(auth), req));
    }

    @GetMapping("/transferencias/recibidas")
    @Operation(summary = "Transferencias pendientes donde el usuario es destino")
    public ResponseEntity<List<TransferenciaResponse>> getTransferenciasRecibidas(Authentication auth) {
        return ResponseEntity.ok(usuarioService.getTransferenciasRecibidas(usuarioId(auth)));
    }

    @PostMapping("/transferencias/{id}/aceptar")
    @Operation(summary = "Aceptar una transferencia recibida")
    public ResponseEntity<Void> aceptarTransferencia(@PathVariable Long id, Authentication auth) {
        usuarioService.aceptarTransferencia(usuarioId(auth), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferencias/{id}/rechazar")
    @Operation(summary = "Rechazar una transferencia recibida")
    public ResponseEntity<Void> rechazarTransferencia(@PathVariable Long id, Authentication auth) {
        usuarioService.rechazarTransferencia(usuarioId(auth), id);
        return ResponseEntity.ok().build();
    }

    private Long usuarioId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
