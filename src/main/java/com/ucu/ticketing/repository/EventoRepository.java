package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByEstadioAdminPaisUsuarioId(Long adminPaisId);

    @Query("SELECT e FROM Evento e WHERE e.fechaHora >= :desde ORDER BY e.fechaHora ASC")
    List<Evento> findProximos(@Param("desde") LocalDateTime desde);

    @Query("""
        SELECT e FROM Evento e
        WHERE e.fechaHora >= :desde
          AND (:pais IS NULL OR e.estadio.pais = :pais)
          AND (:estadioId IS NULL OR e.estadio.id = :estadioId)
        ORDER BY e.fechaHora ASC
        """)
    List<Evento> findProximosFiltrados(
            @Param("desde") LocalDateTime desde,
            @Param("pais") String pais,
            @Param("estadioId") Long estadioId);
}
