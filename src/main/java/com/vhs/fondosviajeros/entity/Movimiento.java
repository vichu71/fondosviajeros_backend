package com.vhs.fondosviajeros.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vhs.fondosviajeros.entity.enume.TipoMovimiento;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto;

    private double cantidad; // siempre positiva

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo; // APORTE o GASTO

    @ManyToOne
    // ✅ CORREGIDO: Cambiar "fondos" por "usuarioFondos"
    @JsonIgnoreProperties({"usuarioFondos", "movimientos"})
    private Usuario usuario;

    @ManyToOne
    // ✅ ACTUALIZADO: Incluir "usuarioFondos" también por consistencia
    @JsonIgnoreProperties({"movimientos", "usuarioFondos"})
    private Fondo fondo;
}