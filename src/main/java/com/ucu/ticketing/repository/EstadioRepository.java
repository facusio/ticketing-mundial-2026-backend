package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Estadio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadioRepository extends JpaRepository<Estadio, Long> {
    List<Estadio> findByAdminPaisUsuarioId(Long adminPaisId);
}
