package com.vhs.fondosviajeros.repository;

import com.vhs.fondosviajeros.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByFondo_Id(Long fondoId);
}
