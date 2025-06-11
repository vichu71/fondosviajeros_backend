package com.vhs.fondosviajeros.service;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Movimiento;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.repository.MovimientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Transactional
    public Movimiento crearMovimiento(String concepto, double cantidad, Usuario usuario, Fondo fondo) {
        Movimiento movimiento = new Movimiento();
        movimiento.setConcepto(concepto);
        movimiento.setCantidad(cantidad); // positivo o negativo
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);
        movimiento.setFondo(fondo);
        return movimientoRepository.save(movimiento);
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
}
