package com.vhs.fondosviajeros.service;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Movimiento;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.entity.enume.TipoMovimiento;
import com.vhs.fondosviajeros.repository.MovimientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Transactional
    public Movimiento crearMovimiento(String concepto, Double cantidad, String tipo, Usuario usuario, Fondo fondo) {
        Movimiento movimiento = new Movimiento();
        movimiento.setConcepto(concepto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(TipoMovimiento.valueOf(tipo)); // Convertir String a Enum
        movimiento.setUsuario(usuario);
        movimiento.setFondo(fondo);
        movimiento.setFecha(LocalDateTime.now());
        
        System.out.println("💾 Guardando movimiento en BD...");
        
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        
       
        return movimientoGuardado;
    }

    public List<Movimiento> obtenerPorFondo(Long fondoId) {
        return movimientoRepository.findByFondo_Id(fondoId);
    }

    public double obtenerTotalPorFondo(Long fondoId) {
        return movimientoRepository.findByFondo_Id(fondoId)
                .stream()
                .mapToDouble(Movimiento::getCantidad)
                .sum();
    }

	public Page<Movimiento> findMovimientosByFondo(Long fondoId, Pageable pageable) {
		return movimientoRepository.findByFondo(fondoId, pageable);
	}
}
