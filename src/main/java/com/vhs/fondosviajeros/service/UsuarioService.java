package com.vhs.fondosviajeros.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.entity.UsuarioFondo;
import com.vhs.fondosviajeros.entity.enume.Rol;
import com.vhs.fondosviajeros.repository.UsuarioRepository;
import com.vhs.fondosviajeros.repository.UsuarioFondoRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
	
	private static final String AVATAR_BASE64_EJEMPLO =
		    "iVBORw0KGgoAAAANSUhEUgAAAAUA" +
		    "AAAFCAYAAACNbyblAAAAHElEQVQI12P4" +
		    "//8/w38GIAXDIBKE0DHxgljNBAAO" +
		    "9TXL0Y4OHwAAAABJRU5ErkJggg==";
	
	private byte[] getAvatarPorDefecto() {
	    return Base64.getDecoder().decode(AVATAR_BASE64_EJEMPLO);
	}

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // ✅ AÑADIDO: Repository para manejar relaciones Usuario-Fondo
    @Autowired
    private UsuarioFondoRepository usuarioFondoRepository;

    // ✅ CORRECTO: Método para crear admin (ya estaba bien)
    @Transactional
    public Usuario crearUsuarioAdmin(String nombre, Fondo fondo, String uuidDispositivo) {
        // Buscar usuario existente por UUID
        Optional<Usuario> existente = usuarioRepository.findByUuidDispositivo(uuidDispositivo);
        
        Usuario usuario;
        if (existente.isPresent()) {
            usuario = existente.get();
            usuario.setNombre(nombre);
            usuario = usuarioRepository.save(usuario);
        } else {
            // Crear nuevo usuario (SIN asignar rol aquí)
            usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setAvatar(getAvatarPorDefecto());
            usuario.setUuidDispositivo(uuidDispositivo);
            usuario = usuarioRepository.save(usuario);
        }
        
        // Verificar si ya existe la relación usuario-fondo
        Optional<UsuarioFondo> relacionExistente = usuarioFondoRepository
            .findByIdUsuarioIdAndIdFondoId(usuario.getId(), fondo.getId());
            
        if (relacionExistente.isEmpty()) {
            // Crear relación UsuarioFondo con rol ADMIN
            UsuarioFondo usuarioFondo = new UsuarioFondo(usuario, fondo, Rol.ADMIN);
            usuarioFondoRepository.save(usuarioFondo);
        }
        
        return usuario;
    }

    // ✅ CORREGIDO: Método original actualizado para nueva estructura
    @Transactional
    public Usuario crearUsuarioEnFondo(String nombre, Fondo fondo) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        // ❌ ELIMINADO: usuario.setRol(Rol.USER);
        usuario.setAvatar(getAvatarPorDefecto());
        // ❌ ELIMINADO: usuario.setFondos(Collections.singletonList(fondo));
        
        // Guardar usuario primero
        usuario = usuarioRepository.save(usuario);
        
        // ✅ NUEVO: Crear relación UsuarioFondo con rol USER
        UsuarioFondo usuarioFondo = new UsuarioFondo(usuario, fondo, Rol.USER);
        usuarioFondoRepository.save(usuarioFondo);
        
        return usuario;
    }

    // ✅ CORREGIDO: Método con UUID actualizado para nueva estructura
    @Transactional
    public Usuario crearUsuarioEnFondo(String nombre, Fondo fondo, String uuidDispositivo) {
        // Buscar usuario existente por UUID
        Optional<Usuario> existente = usuarioRepository.findByUuidDispositivo(uuidDispositivo);
        
        Usuario usuario;
        if (existente.isPresent()) {
            usuario = existente.get();
            usuario.setNombre(nombre);
            usuario = usuarioRepository.save(usuario);
        } else {
            // Crear nuevo usuario (SIN asignar rol aquí)
            usuario = new Usuario();
            usuario.setNombre(nombre);
            // ❌ ELIMINADO: usuario.setRol(Rol.USER);
            usuario.setAvatar(getAvatarPorDefecto());
            usuario.setUuidDispositivo(uuidDispositivo);
            usuario = usuarioRepository.save(usuario);
        }
        
        // ✅ CORREGIDO: Verificar si ya existe la relación usuario-fondo
        Optional<UsuarioFondo> relacionExistente = usuarioFondoRepository
            .findByIdUsuarioIdAndIdFondoId(usuario.getId(), fondo.getId());
            
        if (relacionExistente.isEmpty()) {
            // Crear relación UsuarioFondo con rol USER
            UsuarioFondo usuarioFondo = new UsuarioFondo(usuario, fondo, Rol.USER);
            usuarioFondoRepository.save(usuarioFondo);
        }
        
        return usuario;
    }

    // ✅ CORREGIDO: Usar UsuarioFondoRepository en lugar de UsuarioRepository
    public boolean existeUsuarioEnFondo(String nombreUsuario, Long fondoId) {
        return usuarioFondoRepository.existsUsuarioInFondo(nombreUsuario, fondoId);
    }

    // ✅ CORREGIDO: Usar UsuarioFondoRepository en lugar de UsuarioRepository
    public boolean existeUuidEnFondo(String uuidDispositivo, Long fondoId) {
        if (uuidDispositivo == null || uuidDispositivo.trim().isEmpty()) {
            return false;
        }
        return usuarioFondoRepository.existsUuidInFondo(uuidDispositivo, fondoId);
    }

    // ✅ NUEVOS: Métodos para manejar roles por relación
    public Rol getRolUsuarioEnFondo(Long usuarioId, Long fondoId) {
        return usuarioFondoRepository.findByIdUsuarioIdAndIdFondoId(usuarioId, fondoId)
            .map(UsuarioFondo::getRol)
            .orElse(null);
    }

    public boolean esUsuarioAdminDeFondo(Long usuarioId, Long fondoId) {
        return usuarioFondoRepository.isUsuarioAdminOfFondo(usuarioId, fondoId);
    }

    // ✅ SIN CAMBIOS: Métodos básicos del repository
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // ❌ ELIMINADO: Este método ya no tiene sentido con la nueva estructura
    // public List<Usuario> findByRol(Rol rol) {
    //     return usuarioRepository.findByRol(rol);
    // }

    public Optional<Usuario> findUsuarioEnFondoConNombre(String nombreUsuario, String nombreFondo) {
        return usuarioRepository.findUsuarioEnFondoConNombre(nombreUsuario, nombreFondo);
    }
}