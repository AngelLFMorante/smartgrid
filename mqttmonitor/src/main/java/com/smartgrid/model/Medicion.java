package com.smartgrid.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Medicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreDispositivo;

    private double consumo;

    private LocalDateTime fechaHora;

    public Medicion() {}

    public Medicion(String nombreDispositivo, double consumo, LocalDateTime fechaHora) {
        this.nombreDispositivo = nombreDispositivo;
        this.consumo = consumo;
        this.fechaHora = fechaHora;
    }

    // Getters y Setters
}

