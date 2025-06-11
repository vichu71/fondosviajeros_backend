package com.vhs.fondosviajeros.controller;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.service.FondoService;
import com.vhs.fondosviajeros.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/fondos")
public class FondoController {

    @Autowired
    private FondoService fondoService;

    @Autowired
    private UsuarioService usuarioService;

    // DTOs internos
    public static class CrearFondoRequest {
        public String nombreUsuario;
        public String nombreFondo;
    }

    public static class UnirseFondoRequest {
        public String nombreUsuario;
        public String codigoFondo;
    }

    // POST /api/fondos/crear
    @PostMapping("/crear")
    public ResponseEntity<?> crearFondo(@RequestBody CrearFondoRequest request) {
        Fondo fondo = fondoService.crearFondo(request.nombreFondo);
        Usuario admin = usuarioService.crearUsuarioAdmin(request.nombreUsuario, fondo);
        return ResponseEntity.ok(new FondoRespuesta(fondo, admin));
    }

    // POST /api/fondos/unirse
    @PostMapping("/unirse")
    public ResponseEntity<?> unirseAFondo(@RequestBody UnirseFondoRequest request) {
        Optional<Fondo> fondoOpt = fondoService.findByCodigo(request.codigoFondo);
        if (fondoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Código de fondo no válido");
        }

        Usuario user = usuarioService.crearUsuarioEnFondo(request.nombreUsuario, fondoOpt.get());
        return ResponseEntity.ok(new FondoRespuesta(fondoOpt.get(), user));
    }

    // Clase para devolver respuesta conjunta
    public record FondoRespuesta(Fondo fondo, Usuario usuario) {}
}
