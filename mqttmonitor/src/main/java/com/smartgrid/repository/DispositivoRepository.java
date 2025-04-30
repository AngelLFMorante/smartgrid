package com.smartgrid.repository;

import com.smartgrid.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para acceder a la entidad Dispositivo.
 * Proporciona métodos CRUD automáticos y una búsqueda personalizada por nombre.
 */
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    /**
     * Busca un dispositivo por su nombre.
     *
     * @param nombre nombre del dispositivo
     * @return Optional con el dispositivo si existe
     */
    Optional<Dispositivo> findByNombre(String nombre);
}
