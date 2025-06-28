package com.vhs.fondosviajeros.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vhs.fondosviajeros.entity.Fondo;

public interface FondoRepository extends JpaRepository<Fondo, Long> {
    
    Optional<Fondo> findByCodigo(String codigo);
    
    Optional<Fondo> findByNombre(String nombre);
    
    // ✅ QUERY CORREGIDA: Usa la nueva estructura con UsuarioFondo
    @Query("SELECT f FROM Fondo f JOIN f.usuarioFondos uf WHERE uf.usuario.id = :usuarioId")
    Page<Fondo> findFondosByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
    
    // ✅ OPCIONAL: Query adicional para obtener fondos por usuario y rol específico
    @Query("SELECT f FROM Fondo f JOIN f.usuarioFondos uf WHERE uf.usuario.id = :usuarioId AND uf.rol = :rol")
    Page<Fondo> findFondosByUsuarioIdAndRol(@Param("usuarioId") Long usuarioId, 
                                           @Param("rol") com.vhs.fondosviajeros.entity.enume.Rol rol, 
                                           Pageable pageable);
}