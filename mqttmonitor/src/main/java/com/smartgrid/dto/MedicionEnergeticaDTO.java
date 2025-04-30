package com.smartgrid.dto;

import com.smartgrid.model.TipoMedicion;

/**
 * DTO que representa una medición energética puntual.
 */
public class MedicionEnergeticaDTO {

    private TipoMedicion tipo;
    private double valor;

    public MedicionEnergeticaDTO(TipoMedicion tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoMedicion getTipo() {
        return tipo;
    }

    public void setTipo(TipoMedicion tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Medicion{" + "tipo=" + tipo + ", valor=" + valor + '}';
    }
}
