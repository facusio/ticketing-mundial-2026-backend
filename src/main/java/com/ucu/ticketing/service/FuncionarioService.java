package com.ucu.ticketing.service;

import com.ucu.ticketing.dto.request.DispositivoRequest;
import com.ucu.ticketing.dto.request.ValidacionRequest;
import com.ucu.ticketing.dto.response.ValidacionResponse;
import com.ucu.ticketing.entity.*;
import com.ucu.ticketing.exception.AccesoDenegadoException;
import com.ucu.ticketing.exception.RecursoNoEncontradoException;
import com.ucu.ticketing.exception.ReglaNegocioException;
import com.ucu.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final DispositivoRepository dispositivoRepository;
    private final FuncionarioSectorRepository funcionarioSectorRepository;
    private final TokenQrRepository tokenQrRepository;
    private final EntradaRepository entradaRepository;
    private final ValidacionTernariaRepository validacionTernariaRepository;

    @Transactional
    public void registrarDispositivo(Long funcionarioId, DispositivoRequest req) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Funcionario no encontrado"));

        dispositivoRepository.findByDeviceUidAndFuncionario(req.getDeviceUid(), funcionario)
                .ifPresentOrElse(
                        d -> { /* ya existe, no duplicar */ },
                        () -> {
                            Dispositivo d = Dispositivo.builder()
                                    .funcionario(funcionario)
                                    .deviceUid(req.getDeviceUid())
                                    .build();
                            dispositivoRepository.save(d);
                        }
                );
    }

    public List<Map<String, Object>> getSectoresAsignados(Long funcionarioId) {
        return funcionarioSectorRepository.findByFuncionarioUsuarioId(funcionarioId)
                .stream()
                .map(fs -> Map.<String, Object>of(
                        "sectorId", fs.getSector().getId(),
                        "codigo", fs.getSector().getCodigo(),
                        "capacidadMaxima", fs.getSector().getCapacidadMaxima(),
                        "estadioId", fs.getSector().getEstadio().getId(),
                        "estadioNombre", fs.getSector().getEstadio().getNombre()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ValidacionResponse validar(Long funcionarioId, ValidacionRequest req) {
        // 1. Buscar token QR
        TokenQr token = tokenQrRepository.findByCodigo(req.getCodigoQr())
                .orElseThrow(() -> new RecursoNoEncontradoException("QR no encontrado o inválido"));

        // 2. Validar que esté activo y no expirado
        if (!token.getActivo()) {
            throw new ReglaNegocioException("El QR no está activo");
        }
        if (token.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("El QR está expirado. Pedile al titular que regenere el código");
        }

        // 3. Obtener la entrada
        Entrada entrada = token.getEntrada();

        // 4. Verificar que el sector esté asignado al funcionario
        boolean sectorAutorizado = funcionarioSectorRepository
                .existsByFuncionarioIdAndSectorId(funcionarioId, entrada.getSector().getId());
        if (!sectorAutorizado) {
            throw new AccesoDenegadoException("No estás habilitado para validar entradas de este sector");
        }

        // 5. Verificar que la entrada no esté ya consumida
        if (entrada.getEstado() == EstadoEntrada.CONSUMIDA) {
            throw new ReglaNegocioException("Entrada ya validada: esta entrada fue consumida anteriormente");
        }

        // 6. Obtener el dispositivo
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Funcionario no encontrado"));

        Dispositivo dispositivo = dispositivoRepository.findByDeviceUidAndFuncionario(req.getDeviceUid(), funcionario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dispositivo no registrado. Registralo primero"));

        // 7. Insertar validacion_ternaria (el trigger T6 marcará la entrada como CONSUMIDA)
        ValidacionTernariaId vtId = new ValidacionTernariaId(funcionarioId, dispositivo.getId(), entrada.getId());
        ValidacionTernaria vt = ValidacionTernaria.builder()
                .id(vtId)
                .funcionario(funcionario)
                .dispositivo(dispositivo)
                .entrada(entrada)
                .tokenQr(token)
                .fechaHora(LocalDateTime.now())
                .build();
        validacionTernariaRepository.save(vt);
        validacionTernariaRepository.flush();

        // Refrescar estado de la entrada tras el trigger
        entradaRepository.findById(entrada.getId()).ifPresent(e -> entrada.setEstado(e.getEstado()));

        Usuario propietario = entrada.getPropietarioActual();

        return ValidacionResponse.builder()
                .mensaje("ACCESO PERMITIDO")
                .entradaId(entrada.getId())
                .fechaValidacion(vt.getFechaHora())
                .propietario(ValidacionResponse.PropietarioDto.builder()
                        .id(propietario.getId())
                        .mail(propietario.getMail())
                        .numeroDoc(propietario.getNumeroDoc())
                        .build())
                .evento(ValidacionResponse.EventoDto.builder()
                        .id(entrada.getEvento().getId())
                        .equipoLocal(entrada.getEvento().getEquipoLocal())
                        .equipoVisitante(entrada.getEvento().getEquipoVisitante())
                        .fechaHora(entrada.getEvento().getFechaHora())
                        .build())
                .sector(ValidacionResponse.SectorDto.builder()
                        .id(entrada.getSector().getId())
                        .codigo(entrada.getSector().getCodigo())
                        .estadioNombre(entrada.getSector().getEstadio().getNombre())
                        .build())
                .build();
    }

    public List<ValidacionResponse> getValidaciones(Long funcionarioId) {
        return validacionTernariaRepository.findByFuncionarioId(funcionarioId)
                .stream()
                .map(vt -> {
                    Usuario propietario = vt.getEntrada().getPropietarioActual();
                    return ValidacionResponse.builder()
                            .entradaId(vt.getEntrada().getId())
                            .fechaValidacion(vt.getFechaHora())
                            .propietario(ValidacionResponse.PropietarioDto.builder()
                                    .id(propietario.getId())
                                    .mail(propietario.getMail())
                                    .numeroDoc(propietario.getNumeroDoc())
                                    .build())
                            .evento(ValidacionResponse.EventoDto.builder()
                                    .id(vt.getEntrada().getEvento().getId())
                                    .equipoLocal(vt.getEntrada().getEvento().getEquipoLocal())
                                    .equipoVisitante(vt.getEntrada().getEvento().getEquipoVisitante())
                                    .fechaHora(vt.getEntrada().getEvento().getFechaHora())
                                    .build())
                            .sector(ValidacionResponse.SectorDto.builder()
                                    .id(vt.getEntrada().getSector().getId())
                                    .codigo(vt.getEntrada().getSector().getCodigo())
                                    .estadioNombre(vt.getEntrada().getSector().getEstadio().getNombre())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
