package com.vhs.fondosviajeros.controller;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Movimiento;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.service.FondoService;
import com.vhs.fondosviajeros.service.MovimientoService;
import com.vhs.fondosviajeros.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> crearMovimiento(@RequestBody Map<String, Object> request) {
        try {
            // Extraer datos del JSON recibido de Flutter
            String concepto = (String) request.get("concepto");
            Double cantidad = Double.valueOf(request.get("cantidad").toString());
            String tipo = (String) request.get("tipo");
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            Long fondoId = Long.valueOf(request.get("fondoId").toString());
            
            System.out.println("🚀 [POST] Creando movimiento:");
            System.out.println("📝 Concepto: " + concepto);
            System.out.println("💰 Cantidad: " + cantidad);
            System.out.println("🏷️ Tipo: " + tipo);
            System.out.println("👤 Usuario ID: " + usuarioId);
            System.out.println("💰 Fondo ID: " + fondoId);

            // Validar que los datos no sean nulos
            if (concepto == null || cantidad == null || usuarioId == null || fondoId == null) {
                return ResponseEntity.badRequest().body("Datos incompletos en la solicitud");
            }

            // Buscar usuario y fondo
            Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioId);
            Optional<Fondo> fondoOpt = fondoService.findById(fondoId);

            if (usuarioOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado: " + usuarioId);
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            if (fondoOpt.isEmpty()) {
                System.out.println("❌ Fondo no encontrado: " + fondoId);
                return ResponseEntity.badRequest().body("Fondo no encontrado");
            }

            // Crear el movimiento
            Movimiento mov = movimientoService.crearMovimiento(
                concepto,
                cantidad,
                tipo, // Pasar el tipo (APORTE/GASTO)
                usuarioOpt.get(),
                fondoOpt.get()
            );

            System.out.println("✅ Movimiento creado exitosamente: " + mov.getId());
            
            return ResponseEntity.ok(mov);
            
        } catch (Exception e) {
            System.err.println("💥 Error al crear movimiento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al crear movimiento: " + e.getMessage());
        }
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
    @GetMapping("/fondo/{fondoId}")
    public ResponseEntity<?> getMovimientosByFondo(
            @PathVariable Long fondoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            System.out.println("🚀 Obteniendo movimientos para fondo ID: " + fondoId);
            System.out.println("📄 Página: " + page + ", Tamaño: " + size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Movimiento> movimientos = movimientoService.findMovimientosByFondo(fondoId, pageable);
            
            System.out.println("✅ Movimientos encontrados: " + movimientos.getTotalElements());
            
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener movimientos: " + e.getMessage());
            e.printStackTrace(); // Esto te mostrará el stack trace completo
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener movimientos: " + e.getMessage());
        }
    }
}
