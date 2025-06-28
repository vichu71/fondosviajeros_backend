package com.vhs.fondosviajeros.repository;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Movimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByFondo_Id(Long fondoId);
   
    @Query("SELECT m FROM Movimiento m WHERE m.fondo.id = :fondoId ORDER BY m.fecha DESC")
    Page<Movimiento> findByFondo(@Param("fondoId") Long fondoId, Pageable pageable);
}
