package com.smartgrid.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una incidencia detectada en la red, como una oscilación de voltaje.
 */
@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha y hora en la que se registró la incidencia */
    private LocalDateTime fechaHora;

    /** Descripción de la incidencia (ej. "Oscilación de voltaje detectada: 245V") */
    private String descripcion;

    /** Nivel de severidad (ej: ALTA, MEDIA, BAJA) */
    private String severidad;

    public Incidencia() {
        this.fechaHora = LocalDateTime.now();
    }

    public Incidencia(String descripcion, String severidad) {
        this.fechaHora = LocalDateTime.now();
        this.descripcion = descripcion;
        this.severidad = severidad;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "fechaHora=" + fechaHora +
                ", descripcion='" + descripcion + '\'' +
                ", severidad='" + severidad + '\'' +
                '}';
    }
}
