package com.vhs.fondosviajeros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vhs.fondosviajeros.entity.UsuarioFondo;
import com.vhs.fondosviajeros.entity.UsuarioFondoId;
import com.vhs.fondosviajeros.entity.enume.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioFondoRepository extends JpaRepository<UsuarioFondo, UsuarioFondoId> {

    // ✅ CORREGIDO: Usar la ruta correcta para los atributos embebidos
    Optional<UsuarioFondo> findByIdUsuarioIdAndIdFondoId(Long usuarioId, Long fondoId);

    // ✅ CORREGIDO: Usar la ruta correcta para el usuarioId embebido
    List<UsuarioFondo> findByIdUsuarioId(Long usuarioId);

    // ✅ CORREGIDO: Usar la ruta correcta para el fondoId embebido
    List<UsuarioFondo> findByIdFondoId(Long fondoId);

    // ✅ CORREGIDO: Usar la ruta correcta para fondoId embebido + rol
    List<UsuarioFondo> findByIdFondoIdAndRol(Long fondoId, Rol rol);

    // Verificar si existe un usuario con ese nombre en un fondo específico
    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END " +
           "FROM UsuarioFondo uf " +
           "WHERE uf.usuario.nombre = :nombreUsuario AND uf.fondo.id = :fondoId")
    boolean existsUsuarioInFondo(@Param("nombreUsuario") String nombreUsuario, 
                               @Param("fondoId") Long fondoId);

    // Verificar si existe un usuario con ese UUID en un fondo específico
    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END " +
           "FROM UsuarioFondo uf " +
           "WHERE uf.usuario.uuidDispositivo = :uuidDispositivo AND uf.fondo.id = :fondoId")
    boolean existsUuidInFondo(@Param("uuidDispositivo") String uuidDispositivo, 
                            @Param("fondoId") Long fondoId);

    // ✅ CORREGIDO: Usar la ruta correcta para fondoId embebido
    long countByIdFondoId(Long fondoId);

    // Verificar si un usuario es admin de un fondo
    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END " +
           "FROM UsuarioFondo uf " +
           "WHERE uf.usuario.id = :usuarioId AND uf.fondo.id = :fondoId AND uf.rol = 'ADMIN'")
    boolean isUsuarioAdminOfFondo(@Param("usuarioId") Long usuarioId, 
                                @Param("fondoId") Long fondoId);
}