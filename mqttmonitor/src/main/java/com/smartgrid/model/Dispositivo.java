package com.smartgrid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa un dispositivo conectado al sistema de Smart Grid.
 * Contiene información básica como nombre, zona, criticidad y consumo (transitorio).
 */
@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La zona no puede estar vacía.")
    private String zona;

    /**
     * Nivel de criticidad del dispositivo: BAJA, MEDIA o CRITICA.
     */
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "La criticidad es obligatoria.")
    private NivelCriticidad criticidad;

    /**
     * Consumo actual del dispositivo (en Watts).
     * No se persiste en la base de datos.
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

    public NivelCriticidad getCriticidad() {
        return criticidad;
    }

    public void setCriticidad(NivelCriticidad criticidad) {
        this.criticidad = criticidad;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }
}
