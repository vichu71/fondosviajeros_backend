package com.vhs.fondosviajeros.service;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.entity.Rol;
import com.vhs.fondosviajeros.entity.Usuario;
import com.vhs.fondosviajeros.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario crearUsuarioAdmin(String nombre, Fondo fondo) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setRol(Rol.ADMIN);
        usuario.setFondos(Collections.singletonList(fondo));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario crearUsuarioEnFondo(String nombre, Fondo fondo) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setRol(Rol.USER);
        usuario.setFondos(Collections.singletonList(fondo));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
}
