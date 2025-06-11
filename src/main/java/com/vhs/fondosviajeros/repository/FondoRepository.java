package com.vhs.fondosviajeros.repository;

import com.vhs.fondosviajeros.entity.Fondo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FondoRepository extends JpaRepository<Fondo, Long> {
    Optional<Fondo> findByCodigo(String codigo);
}
