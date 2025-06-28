package com.vhs.fondosviajeros.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vhs.fondosviajeros.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por UUID del dispositivo
    Optional<Usuario> findByUuidDispositivo(String uuidDispositivo);
    
    // ✅ QUERY CORREGIDA: Usa la nueva estructura con UsuarioFondo
    @Query("SELECT u FROM Usuario u JOIN u.usuarioFondos uf JOIN uf.fondo f " +
           "WHERE u.nombre = :nombreUsuario AND f.nombre = :nombreFondo")
    Optional<Usuario> findUsuarioEnFondoConNombre(@Param("nombreUsuario") String nombreUsuario, 
                                                @Param("nombreFondo") String nombreFondo);

    
}