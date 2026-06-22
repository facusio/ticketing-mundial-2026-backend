package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUsuarioGeneralUsuarioIdOrderByFechaDesc(Long usuarioId);
}
