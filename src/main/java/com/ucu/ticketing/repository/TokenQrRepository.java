package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.TokenQr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenQrRepository extends JpaRepository<TokenQr, Long> {

    Optional<TokenQr> findByCodigo(String codigo);

    Optional<TokenQr> findByEntradaIdAndActivoTrue(Long entradaId);

    @Modifying
    @Query("UPDATE TokenQr t SET t.activo = false WHERE t.entrada.id = :entradaId AND t.activo = true")
    void desactivarTokensActivos(@Param("entradaId") Long entradaId);
}
