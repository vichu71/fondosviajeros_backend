package com.vhs.fondosviajeros.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
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

    private double cantidad; // positivo = aporte, negativo = gasto

    private LocalDateTime fecha;

    @ManyToOne
    @JsonIgnoreProperties({"fondos", "movimientos"})
    private Usuario usuario;

    @ManyToOne
    @JsonIgnoreProperties("movimientos")
    private Fondo fondo;

    // Getters y setters
}
