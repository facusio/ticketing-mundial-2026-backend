package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Entrada;
import com.ucu.ticketing.entity.EstadoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    List<Entrada> findByPropietarioActualId(Long usuarioId);

    @Query("SELECT COUNT(e) FROM Entrada e WHERE e.venta.usuarioGeneral.usuarioId = :usuarioId AND e.evento.id = :eventoId AND e.estado != 'CONSUMIDA'")
    long countEntradasActivasByUsuarioYEvento(@Param("usuarioId") Long usuarioId, @Param("eventoId") Long eventoId);

    @Query("SELECT COUNT(e) FROM Entrada e WHERE e.venta.id = :ventaId")
    long countByVentaId(@Param("ventaId") Long ventaId);
}
