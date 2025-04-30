package com.smartgrid.dto;

/**
 * DTO para representar información básica del dispositivo para la vista.
 * No incluye lógica ni anotaciones JPA.
 */
public class DispositivoDTO {

    private String nombre;
    private String zona;
    private String criticidad;
    private double consumo;

    // Constructor, Getters y Setters
    public DispositivoDTO(String nombre, String zona, String criticidad, double consumo) {
        this.nombre = nombre;
        this.zona = zona;
        this.criticidad = criticidad;
        this.consumo = consumo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getCriticidad() { return criticidad; }
    public void setCriticidad(String criticidad) { this.criticidad = criticidad; }

    public double getConsumo() { return consumo; }
    public void setConsumo(double consumo) { this.consumo = consumo; }
}
