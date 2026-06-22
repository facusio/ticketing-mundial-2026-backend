package com.ucu.ticketing.service;

import com.ucu.ticketing.dto.request.LoginRequest;
import com.ucu.ticketing.dto.request.RegisterRequest;
import com.ucu.ticketing.dto.response.LoginResponse;
import com.ucu.ticketing.entity.*;
import com.ucu.ticketing.exception.ReglaNegocioException;
import com.ucu.ticketing.repository.*;
import com.ucu.ticketing.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioGeneralRepository usuarioGeneralRepository;
    private final TelefonoRepository telefonoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterRequest req) {
        if (usuarioRepository.existsByMail(req.getMail())) {
            throw new ReglaNegocioException("Ya existe un usuario con el mail: " + req.getMail());
        }
        if (usuarioRepository.existsByPaisDocAndTipoDocAndNumeroDoc(
                req.getPaisDoc(), req.getTipoDoc(), req.getNumeroDoc())) {
            throw new ReglaNegocioException("Ya existe un usuario con ese documento");
        }

        Usuario usuario = Usuario.builder()
                .mail(req.getMail())
                .password(passwordEncoder.encode(req.getPassword()))
                .paisDoc(req.getPaisDoc())
                .tipoDoc(req.getTipoDoc())
                .numeroDoc(req.getNumeroDoc())
                .paisDir(req.getPaisDir())
                .localidad(req.getLocalidad())
                .calle(req.getCalle())
                .numeroDir(req.getNumeroDir())
                .codigoPostal(req.getCodigoPostal())
                .rol(RolUsuario.USUARIO_GENERAL)
                .build();
        usuarioRepository.save(usuario);

        UsuarioGeneral ug = UsuarioGeneral.builder()
                .usuario(usuario)
                .fechaRegistro(LocalDate.now())
                .estadoVerificacion(UsuarioGeneral.EstadoVerificacion.NO_VERIFICADO)
                .build();
        usuarioGeneralRepository.save(ug);

        req.getTelefonos().forEach(t -> {
            Telefono tel = Telefono.builder()
                    .usuario(usuario)
                    .numero(t.getNumero())
                    .build();
            telefonoRepository.save(tel);
        });
    }

    public LoginResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getMail(), req.getPassword())
        );

        Usuario usuario = usuarioRepository.findByMail(req.getMail())
                .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(usuario.getId(), usuario.getRol().name());
        return new LoginResponse(token, usuario.getRol().name(), usuario.getId());
    }
}
