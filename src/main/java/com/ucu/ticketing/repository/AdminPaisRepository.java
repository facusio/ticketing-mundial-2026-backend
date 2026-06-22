package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.AdminPais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPaisRepository extends JpaRepository<AdminPais, Long> {
}
