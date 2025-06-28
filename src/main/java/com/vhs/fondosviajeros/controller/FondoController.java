package com.vhs.fondosviajeros.controller;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.entity.enume.Rol;
import com.vhs.fondosviajeros.service.FondoService;
import com.vhs.fondosviajeros.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fondos")
@CrossOrigin
public class FondoController {

    @Autowired
    private FondoService fondoService;

    @Autowired
    private UsuarioService usuarioService;

    // DTOs internos
    public static class CrearFondoRequest {
        public String nombreUsuario;
        public String nombreFondo;
        public String uuidDispositivo;
    }

    public static class UnirseFondoRequest {
        public String nombreUsuario;
        public String codigoFondo;
        public String uuidDispositivo;
    }

    // DTO para respuestas con mensaje
    public static class ApiResponse {
        public String mensaje;
        public Object data;
        
        public ApiResponse(String mensaje, Object data) {
            this.mensaje = mensaje;
            this.data = data;
        }
        
        public ApiResponse(String mensaje) {
            this.mensaje = mensaje;
            this.data = null;
        }
    }

    // ✅ NUEVO: DTO para Usuario con rol contextual
    public static class UsuarioConRol {
        public Long id;
        public String nombre;
        public String rol;  // ← Rol específico para este fondo
        public String avatar;
        public String uuidDispositivo;
        
        public UsuarioConRol(Usuario usuario, Rol rolEnFondo) {
            this.id = usuario.getId();
            this.nombre = usuario.getNombre();
            this.rol = rolEnFondo != null ? rolEnFondo.toString() : "USER";
            this.avatar = usuario.getAvatar() != null ? 
                java.util.Base64.getEncoder().encodeToString(usuario.getAvatar()) : null;
            this.uuidDispositivo = usuario.getUuidDispositivo();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearFondo(@RequestBody CrearFondoRequest request) {
        Optional<Usuario> usuarioExistente = usuarioService.findUsuarioEnFondoConNombre(request.nombreUsuario, request.nombreFondo);
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse("Ya existe un fondo con ese nombre creado por este usuario."));
        }

        Optional<Fondo> fondoExistente = fondoService.findByNombre(request.nombreFondo);
        if (fondoExistente.isPresent()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse("Ya existe un fondo con ese nombre."));
        }

        Fondo fondo = fondoService.crearFondo(request.nombreFondo);
        Usuario admin = usuarioService.crearUsuarioAdmin(request.nombreUsuario, fondo, request.uuidDispositivo);
        
        // ✅ NUEVO: Crear respuesta con rol contextual
        UsuarioConRol usuarioConRol = new UsuarioConRol(admin, Rol.ADMIN);
        
        return ResponseEntity.ok(new FondoRespuestaConRol(fondo, usuarioConRol, "Fondo creado exitosamente"));
    }

    @PostMapping("/unirse")
    public ResponseEntity<?> unirseAFondo(@RequestBody UnirseFondoRequest request) {
        try {
            // 1. Validar que el fondo existe
            Optional<Fondo> fondoOpt = fondoService.findByCodigo(request.codigoFondo);
            if (fondoOpt.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(new ApiResponse("Código de fondo no válido"));
            }

            Fondo fondo = fondoOpt.get();

            // 2. Validar que el usuario no esté ya en este fondo
            boolean usuarioYaEnFondo = usuarioService.existeUsuarioEnFondo(request.nombreUsuario, fondo.getId());
            if (usuarioYaEnFondo) {
                return ResponseEntity.status(409)
                    .body(new ApiResponse("Este usuario ya forma parte del fondo"));
            }

            // 3. Validar que no exista otro usuario con el mismo UUID en este fondo
            if (request.uuidDispositivo != null) {
                boolean uuidYaEnFondo = usuarioService.existeUuidEnFondo(request.uuidDispositivo, fondo.getId());
                if (uuidYaEnFondo) {
                    return ResponseEntity.status(409)
                        .body(new ApiResponse("Este dispositivo ya está asociado a otro usuario en el fondo"));
                }
            }

            // 4. Crear usuario con rol USER en el fondo
            Usuario nuevoUsuario = usuarioService.crearUsuarioEnFondo(
                request.nombreUsuario, 
                fondo, 
                request.uuidDispositivo
            );

            // ✅ NUEVO: Crear respuesta con rol contextual
            UsuarioConRol usuarioConRol = new UsuarioConRol(nuevoUsuario, Rol.USER);

            // 5. Respuesta exitosa
            return ResponseEntity.ok(new FondoRespuestaConRol(
                fondo, 
                usuarioConRol, 
                "Usuario unido exitosamente al fondo"
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse("Datos inválidos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse("Error interno del servidor"));
        }
    }

    // ✅ NUEVO: DTO para Fondo con rol contextual del usuario
    public static class FondoConRol {
        public Long id;
        public String nombre;
        public String codigo;
        public java.time.LocalDateTime fechaCreacion;
        public String monto;
        public String rolUsuario;  // ← Rol del usuario en este fondo específico
        
        public FondoConRol(Fondo fondo, Rol rolUsuario) {
            this.id = fondo.getId();
            this.nombre = fondo.getNombre();
            this.codigo = fondo.getCodigo();
            this.fechaCreacion = fondo.getFechaCreacion();
            this.monto = fondo.getMontoFormateado();
            this.rolUsuario = rolUsuario != null ? rolUsuario.toString() : "USER";
        }
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> getFondosByUsuario(
        @PathVariable Long usuarioId,
        Pageable pageable
    ) {
        try {
            // Obtener fondos del usuario
            Page<Fondo> fondosPage = fondoService.findFondosByUsuario(usuarioId, pageable);
            
            // Convertir a FondoConRol incluyendo el rol específico en cada fondo
            List<FondoConRol> fondosConRol = fondosPage.getContent().stream()
                .map(fondo -> {
                    Rol rolEnFondo = usuarioService.getRolUsuarioEnFondo(usuarioId, fondo.getId());
                    return new FondoConRol(fondo, rolEnFondo);
                })
                .toList();
            
            // Crear página con la nueva estructura
            return ResponseEntity.ok(new FondoConRolPage(
                fondosConRol,
                fondosPage.isLast(),
                (int) fondosPage.getTotalElements(),
                fondosPage.getTotalPages(),
                fondosPage.isFirst(),
                fondosPage.getSize(),
                fondosPage.getNumber(),
                fondosPage.isEmpty()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse("Error al obtener fondos del usuario"));
        }
    }

    // ✅ NUEVO: Clase para devolver página de fondos con rol
    public static class FondoConRolPage {
        public List<FondoConRol> content;
        public boolean last;
        public int totalElements;
        public int totalPages;
        public boolean first;
        public int size;
        public int number;
        public boolean empty;
        
        public FondoConRolPage(List<FondoConRol> content, boolean last, int totalElements, 
                              int totalPages, boolean first, int size, int number, boolean empty) {
            this.content = content;
            this.last = last;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = first;
            this.size = size;
            this.number = number;
            this.empty = empty;
        }
    }

    // ✅ NUEVO: Respuesta con usuario que incluye rol contextual
    public record FondoRespuestaConRol(Fondo fondo, UsuarioConRol usuario, String mensaje) {
        // Constructor para compatibilidad hacia atrás (sin mensaje)
        public FondoRespuestaConRol(Fondo fondo, UsuarioConRol usuario) {
            this(fondo, usuario, "Operación exitosa");
        }
    }
    
    // ✅ MANTENER: Respuesta original para otras operaciones que no necesiten rol
    public record FondoRespuesta(Fondo fondo, Usuario usuario, String mensaje) {
        public FondoRespuesta(Fondo fondo, Usuario usuario) {
            this(fondo, usuario, "Operación exitosa");
        }
    }
}