package com.smartgrid.model;

/**
 * Enum que representa el nivel de criticidad de un dispositivo.
 * CRITICA: No debe ser desconectado automáticamente.
 * MEDIA: Puede ser desconectado si es necesario.
 * BAJA: Puede ser desconectado sin restricciones.
 */
public enum NivelCriticidad {
    BAJA,
    MEDIA,
    CRITICA
}
