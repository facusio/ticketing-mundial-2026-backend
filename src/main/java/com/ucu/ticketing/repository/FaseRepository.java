package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {
}
