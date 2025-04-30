package com.smartgrid.repository;

import com.smartgrid.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar el almacenamiento y consulta de incidencias.
 */
@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
}
