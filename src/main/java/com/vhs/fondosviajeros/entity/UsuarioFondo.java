package com.vhs.fondosviajeros.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vhs.fondosviajeros.entity.enume.Rol;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario_fondo")
public class UsuarioFondo {

    @EmbeddedId
    private UsuarioFondoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"fondos", "usuarioFondos"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fondoId")
    @JoinColumn(name = "fondo_id")
    @JsonIgnoreProperties({"usuarios", "usuarioFondos", "movimientos"})
    private Fondo fondo;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol = Rol.USER;

    // Constructores
    public UsuarioFondo() {}

    public UsuarioFondo(Usuario usuario, Fondo fondo, Rol rol) {
        this.usuario = usuario;
        this.fondo = fondo;
        this.rol = rol;
        this.id = new UsuarioFondoId(usuario.getId(), fondo.getId());
    }

    // Métodos de conveniencia
    public Long getUsuarioId() {
        return id != null ? id.getUsuarioId() : null;
    }

    public Long getFondoId() {
        return id != null ? id.getFondoId() : null;
    }
}