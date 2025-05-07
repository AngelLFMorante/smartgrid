package com.smartgrid.repository;

import com.smartgrid.model.Medicion;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, Long> {
    void deleteByFechaHoraBefore(LocalDateTime fechaLimite);
}
