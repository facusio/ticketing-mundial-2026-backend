package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.ValidacionTernaria;
import com.ucu.ticketing.entity.ValidacionTernariaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidacionTernariaRepository extends JpaRepository<ValidacionTernaria, ValidacionTernariaId> {

    @Query("SELECT v FROM ValidacionTernaria v WHERE v.funcionario.usuarioId = :funcionarioId ORDER BY v.fechaHora DESC")
    List<ValidacionTernaria> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);
}
