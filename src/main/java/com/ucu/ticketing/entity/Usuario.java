package com.ucu.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario", schema = "ticketing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mail", nullable = false, unique = true, length = 255)
    private String mail;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "pais_doc", nullable = false, length = 100)
    private String paisDoc;

    @Column(name = "tipo_doc", nullable = false, length = 50)
    private String tipoDoc;

    @Column(name = "numero_doc", nullable = false, length = 100)
    private String numeroDoc;

    @Column(name = "pais_dir", nullable = false, length = 100)
    private String paisDir;

    @Column(name = "localidad", nullable = false, length = 150)
    private String localidad;

    @Column(name = "calle", nullable = false, length = 200)
    private String calle;

    @Column(name = "numero_dir", nullable = false, length = 20)
    private String numeroDir;

    @Column(name = "codigo_postal", nullable = false, length = 20)
    private String codigoPostal;

    @Column(name = "rol", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;
}
