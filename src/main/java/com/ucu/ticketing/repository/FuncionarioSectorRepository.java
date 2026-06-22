package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.FuncionarioSector;
import com.ucu.ticketing.entity.FuncionarioSectorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioSectorRepository extends JpaRepository<FuncionarioSector, FuncionarioSectorId> {

    List<FuncionarioSector> findByFuncionarioUsuarioId(Long funcionarioId);

    @Query("SELECT COUNT(fs) > 0 FROM FuncionarioSector fs WHERE fs.funcionario.usuarioId = :funcionarioId AND fs.sector.id = :sectorId")
    boolean existsByFuncionarioIdAndSectorId(@Param("funcionarioId") Long funcionarioId, @Param("sectorId") Long sectorId);
}
