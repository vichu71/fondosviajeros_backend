package com.vhs.fondosviajeros.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vhs.fondosviajeros.entity.enume.Rol;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // ❌ ELIMINADO: Campo rol ya no está aquí
    // @Enumerated(EnumType.STRING)
    // private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UsuarioFondo> usuarioFondos = new ArrayList<>();
    
    @Lob
    @Column(name = "avatar")
    private byte[] avatar;
    
    @Column(name = "uuid_dispositivo", unique = true)
    private String uuidDispositivo;

    // Métodos de conveniencia para obtener fondos
    @JsonIgnore
    public List<Fondo> getFondos() {
        return usuarioFondos.stream()
                .map(UsuarioFondo::getFondo)
                .toList();
    }

    // Método para obtener el rol en un fondo específico
    public Rol getRolEnFondo(Long fondoId) {
        return usuarioFondos.stream()
                .filter(uf -> uf.getFondoId().equals(fondoId))
                .map(UsuarioFondo::getRol)
                .findFirst()
                .orElse(null);
    }

    // Método para verificar si es admin en un fondo
    public boolean esAdminEnFondo(Long fondoId) {
        return usuarioFondos.stream()
                .anyMatch(uf -> uf.getFondoId().equals(fondoId) && 
                              uf.getRol() == Rol.ADMIN);
    }
}