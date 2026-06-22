package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.FaseSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaseSectorRepository extends JpaRepository<FaseSector, Long> {

    @Query("SELECT fs FROM FaseSector fs WHERE fs.fase.id = :faseId AND fs.sector.estadio.id = :estadioId")
    List<FaseSector> findByFaseIdAndEstadioId(@Param("faseId") Long faseId, @Param("estadioId") Long estadioId);

    Optional<FaseSector> findByFaseIdAndSectorId(Long faseId, Long sectorId);
}
