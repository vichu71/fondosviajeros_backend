package com.vhs.fondosviajeros.entity;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhs.fondosviajeros.entity.enume.Rol;
import com.vhs.fondosviajeros.entity.enume.TipoMovimiento;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Fondo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String codigo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "fondo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Movimiento> movimientos = new ArrayList<>();

    @OneToMany(mappedBy = "fondo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UsuarioFondo> usuarioFondos = new ArrayList<>();

    // Campo calculado y serializado como "monto"
    @JsonProperty("monto")
    @Transient
    public String getMontoFormateado() {
        if (movimientos == null || movimientos.isEmpty()) return "0.00";

        double total = movimientos.stream()
                .mapToDouble(m -> m.getTipo() == TipoMovimiento.APORTE
                        ? m.getCantidad()
                        : -m.getCantidad())
                .sum();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(total);
    }

    // Métodos de conveniencia para obtener usuarios
    @JsonIgnore
    public List<Usuario> getUsuarios() {
        return usuarioFondos.stream()
                .map(UsuarioFondo::getUsuario)
                .toList();
    }

    // Obtener solo los administradores del fondo
    @JsonIgnore
    public List<Usuario> getAdministradores() {
        return usuarioFondos.stream()
                .filter(uf -> uf.getRol() == Rol.ADMIN)
                .map(UsuarioFondo::getUsuario)
                .toList();
    }

    // Verificar si un usuario es admin del fondo
    public boolean esUsuarioAdmin(Long usuarioId) {
        return usuarioFondos.stream()
                .anyMatch(uf -> uf.getUsuarioId().equals(usuarioId) && 
                              uf.getRol() == Rol.ADMIN);
    }

    // Contar miembros del fondo
    public int getNumeroMiembros() {
        return usuarioFondos.size();
    }
}