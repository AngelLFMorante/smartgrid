package com.smartgrid.repository;

import com.smartgrid.model.Medicion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, Long> {
    void deleteByFechaHoraBefore(LocalDateTime fechaLimite);

    @Query("SELECT m.fechaHora, SUM(m.consumo) " +
            "FROM Medicion m " +
            "WHERE m.fechaHora >= :inicio " +
            "GROUP BY FUNCTION('date_trunc', 'minute', m.fechaHora) " +
            "ORDER BY FUNCTION('date_trunc', 'minute', m.fechaHora) ASC")
    List<Object[]> obtenerHistorialDeConsumoPorMinuto(LocalDateTime inicio);

    @Query(value = """
        SELECT SUM(m.consumo)
        FROM Medicion m
        GROUP BY FUNCTION('DATE_TRUNC', 'minute', m.fechaHora)
        ORDER BY FUNCTION('DATE_TRUNC', 'minute', m.fechaHora)
    """)
    List<Double> getConsumoTotalAgrupadoPorMinuto();

}
