package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Long> {
    List<Telefono> findByUsuarioId(Long usuarioId);
}
