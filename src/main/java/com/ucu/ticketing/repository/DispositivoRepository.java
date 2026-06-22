package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Dispositivo;
import com.ucu.ticketing.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    Optional<Dispositivo> findByDeviceUid(String deviceUid);
    Optional<Dispositivo> findByDeviceUidAndFuncionario(String deviceUid, Funcionario funcionario);
}
