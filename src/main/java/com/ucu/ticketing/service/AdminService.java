package com.ucu.ticketing.service;

import com.ucu.ticketing.dto.request.*;
import com.ucu.ticketing.dto.response.EstadioResponse;
import com.ucu.ticketing.dto.response.EventoResponse;
import com.ucu.ticketing.entity.*;
import com.ucu.ticketing.exception.RecursoNoEncontradoException;
import com.ucu.ticketing.exception.ReglaNegocioException;
import com.ucu.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminPaisRepository adminPaisRepository;
    private final EstadioRepository estadioRepository;
    private final SectorRepository sectorRepository;
    private final FaseRepository faseRepository;
    private final FaseSectorRepository faseSectorRepository;
    private final EventoRepository eventoRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public EstadioResponse crearEstadio(Long adminId, EstadioRequest req) {
        AdminPais admin = adminPaisRepository.findById(adminId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Admin no encontrado"));

        Estadio estadio = Estadio.builder()
                .adminPais(admin)
                .nombre(req.getNombre())
                .ciudad(req.getCiudad())
                .pais(req.getPais())
                .build();
        estadioRepository.save(estadio);

        return toEstadioResponse(estadio);
    }

    public List<EstadioResponse> getEstadios(Long adminId) {
        return estadioRepository.findByAdminPaisUsuarioId(adminId)
                .stream()
                .map(this::toEstadioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void crearSector(Long adminId, Long estadioId, SectorRequest req) {
        Estadio estadio = estadioRepository.findById(estadioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estadio no encontrado"));

        if (!estadio.getAdminPais().getUsuarioId().equals(adminId)) {
            throw new ReglaNegocioException("Este estadio no te pertenece");
        }

        Sector sector = Sector.builder()
                .estadio(estadio)
                .codigo(req.getCodigo())
                .capacidadMaxima(req.getCapacidadMaxima())
                .build();
        sectorRepository.save(sector);
    }

    @Transactional
    public EventoResponse crearEvento(Long adminId, EventoRequest req) {
        AdminPais admin = adminPaisRepository.findById(adminId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Admin no encontrado"));

        Estadio estadio = estadioRepository.findById(req.getEstadioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estadio no encontrado"));

        if (!estadio.getAdminPais().getUsuarioId().equals(adminId)) {
            throw new ReglaNegocioException("Este estadio no te pertenece");
        }

        Fase fase = faseRepository.findById(req.getFaseId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Fase no encontrada"));

        Evento evento = Evento.builder()
                .estadio(estadio)
                .admin(admin)
                .fase(fase)
                .fechaHora(req.getFechaHora())
                .equipoLocal(req.getEquipoLocal())
                .equipoVisitante(req.getEquipoVisitante())
                .build();

        try {
            eventoRepository.save(evento);
            eventoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ReglaNegocioException("Ya existe un evento en ese estadio que se superpone con el horario indicado");
        }

        return toEventoResponse(evento);
    }

    public List<EventoResponse> getEventos(Long adminId) {
        return eventoRepository.findByEstadioAdminPaisUsuarioId(adminId)
                .stream()
                .map(this::toEventoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Fase crearFase(FaseRequest req) {
        Fase fase = Fase.builder()
                .nombre(req.getNombre())
                .orden(req.getOrden())
                .build();
        return faseRepository.save(fase);
    }

    @Transactional
    public void definirPrecio(Long faseId, FaseSectorRequest req) {
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Fase no encontrada"));
        Sector sector = sectorRepository.findById(req.getSectorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado"));

        FaseSector fs = faseSectorRepository.findByFaseIdAndSectorId(faseId, req.getSectorId())
                .orElseGet(FaseSector::new);
        fs.setFase(fase);
        fs.setSector(sector);
        fs.setPrecio(req.getPrecio());
        faseSectorRepository.save(fs);
    }

    public List<Map<String, Object>> getRankingEventos() {
        return jdbcTemplate.queryForList("SELECT * FROM ticketing.v_ranking_eventos");
    }

    public List<Map<String, Object>> getRankingCompradores() {
        return jdbcTemplate.queryForList("SELECT * FROM ticketing.v_ranking_compradores");
    }

    public List<Map<String, Object>> getAuditoriaFuncionarios() {
        return jdbcTemplate.queryForList("SELECT * FROM ticketing.v_auditoria_funcionarios");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private EstadioResponse toEstadioResponse(Estadio e) {
        return EstadioResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .ciudad(e.getCiudad())
                .pais(e.getPais())
                .build();
    }

    private EventoResponse toEventoResponse(Evento e) {
        return EventoResponse.builder()
                .id(e.getId())
                .fechaHora(e.getFechaHora())
                .equipoLocal(e.getEquipoLocal())
                .equipoVisitante(e.getEquipoVisitante())
                .estadio(EventoResponse.EstadioDto.builder()
                        .id(e.getEstadio().getId())
                        .nombre(e.getEstadio().getNombre())
                        .ciudad(e.getEstadio().getCiudad())
                        .pais(e.getEstadio().getPais())
                        .build())
                .fase(EventoResponse.FaseDto.builder()
                        .id(e.getFase().getId())
                        .nombre(e.getFase().getNombre())
                        .orden(e.getFase().getOrden())
                        .build())
                .build();
    }
}
