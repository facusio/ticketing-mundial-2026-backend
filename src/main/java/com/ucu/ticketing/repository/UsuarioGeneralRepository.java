package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.UsuarioGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioGeneralRepository extends JpaRepository<UsuarioGeneral, Long> {
}
