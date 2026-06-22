package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.EstadoTransferencia;
import com.ucu.ticketing.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    List<Transferencia> findByDestinoIdAndEstado(Long destinoId, EstadoTransferencia estado);

    List<Transferencia> findByOrigenIdOrderByFechaHoraDesc(Long origenId);

    @Query("SELECT COUNT(t) FROM Transferencia t WHERE t.entrada.id = :entradaId AND t.estado != 'RECHAZADA'")
    long countTransferenciasActivasByEntrada(@Param("entradaId") Long entradaId);
}
