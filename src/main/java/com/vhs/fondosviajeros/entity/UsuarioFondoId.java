package com.vhs.fondosviajeros.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class UsuarioFondoId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(name = "fondo_id")
    private Long fondoId;

    // Constructor por defecto
    public UsuarioFondoId() {}

    // Constructor con parámetros
    public UsuarioFondoId(Long usuarioId, Long fondoId) {
        this.usuarioId = usuarioId;
        this.fondoId = fondoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioFondoId)) return false;
        UsuarioFondoId that = (UsuarioFondoId) o;
        return Objects.equals(usuarioId, that.usuarioId) && 
               Objects.equals(fondoId, that.fondoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, fondoId);
    }

    @Override
    public String toString() {
        return "UsuarioFondoId{" +
                "usuarioId=" + usuarioId +
                ", fondoId=" + fondoId +
                '}';
    }
}