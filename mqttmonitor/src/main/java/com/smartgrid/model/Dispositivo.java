package com.smartgrid.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un dispositivo de consumo energético.
 */
@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String zona;

    /**
     * Nivel de criticidad del dispositivo.
     * Determina si puede o no ser desconectado automáticamente.
     */
    private NivelCriticidad criticidad;

    /**
     * Consumo actual del dispositivo (en Watts).
     * Marcado como @Transient porque no se guarda en la base de datos.
     */
    @Transient
    private double consumo;

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public NivelCriticidad getCriticidad() {
        return criticidad;
    }

    public void setCriticidad(NivelCriticidad criticidad) {
        this.criticidad = criticidad;
    }

}
