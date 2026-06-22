package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    List<Sector> findByEstadioId(Long estadioId);
}
