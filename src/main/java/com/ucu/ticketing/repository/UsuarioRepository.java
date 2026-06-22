package com.ucu.ticketing.repository;

import com.ucu.ticketing.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMail(String mail);
    boolean existsByMail(String mail);
    boolean existsByPaisDocAndTipoDocAndNumeroDoc(String paisDoc, String tipoDoc, String numeroDoc);
}
