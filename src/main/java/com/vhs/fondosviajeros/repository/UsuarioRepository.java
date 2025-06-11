package com.vhs.fondosviajeros.repository;

import com.vhs.fondosviajeros.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}