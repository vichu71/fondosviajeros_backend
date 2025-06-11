package com.vhs.fondosviajeros.controller;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Movimiento;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.service.FondoService;
import com.vhs.fondosviajeros.service.MovimientoService;
import com.vhs.fondosviajeros.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FondoService fondoService;

    // DTO para crear movimientos
    public static class MovimientoRequest {
        public String concepto;
        public double cantidad;
        public Long usuarioId;
        public Long fondoId;
    }

    // POST /api/movimientos
    @PostMapping
    public ResponseEntity<?> crearMovimiento(@RequestBody MovimientoRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(request.usuarioId);
        Optional<Fondo> fondoOpt = fondoService.findById(request.fondoId);

        if (usuarioOpt.isEmpty() || fondoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario o fondo no encontrado");
        }

        Movimiento mov = movimientoService.crearMovimiento(
                request.concepto,
                request.cantidad,
                usuarioOpt.get(),
                fondoOpt.get()
        );

        return ResponseEntity.ok(mov);
    }

    // GET /api/movimientos/{fondoId}
    @GetMapping("/{fondoId}")
    public ResponseEntity<List<Movimiento>> obtenerMovimientos(@PathVariable Long fondoId) {
        List<Movimiento> lista = movimientoService.obtenerPorFondo(fondoId);
        return ResponseEntity.ok(lista);
    }

    // GET /api/movimientos/{fondoId}/total
    @GetMapping("/{fondoId}/total")
    public ResponseEntity<Double> obtenerTotal(@PathVariable Long fondoId) {
        double total = movimientoService.obtenerTotalPorFondo(fondoId);
        return ResponseEntity.ok(total);
    }
}
