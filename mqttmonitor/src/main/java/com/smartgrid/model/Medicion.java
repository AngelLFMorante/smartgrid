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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}

