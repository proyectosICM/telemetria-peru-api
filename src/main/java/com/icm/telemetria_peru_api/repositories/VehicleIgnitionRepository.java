package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VehicleIgnitionRepository extends JpaRepository<VehicleIgnitionModel, Long> {
    List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    VehicleIgnitionModel findTopByVehicleModelOrderByCreatedAtDesc(VehicleModel  vehicleModel);
    List<VehicleIgnitionModel> findByVehicleModelIdOrderByCreatedAt(Long vehicleId);



    El error se debe a que la configuración de MySQL en tu entorno utiliza ONLY_FULL_GROUP_BY, lo que exige que todas las columnas seleccionadas en una consulta estén agregadas o estén incluidas en la cláusula GROUP BY. Esto causa conflictos porque la columna vi.created_at no está en GROUP BY, y tampoco se utiliza dentro de una función de agregación.

            Solución definitiva
    Para solucionar este problema, puedes elegir entre las siguientes opciones:

            1. Modificar la consulta SQL para cumplir con ONLY_FULL_GROUP_BY
    Asegúrate de que todas las columnas seleccionadas estén en la cláusula GROUP BY o sean agregadas. Aquí tienes la consulta corregida:

    java
    Copiar código
    @Query(value = """
    SELECT 
        -- Día
        JSON_OBJECT('day', DATE_FORMAT(vi.created_at, '%Y-%m-%d'), 'arranques', COUNT(vi.status)) AS day_data,
        
        -- Semana
        JSON_OBJECT('week', CONCAT(YEAR(vi.created_at), '-', WEEK(vi.created_at)), 'arranques', COUNT(vi.status)) AS week_data,
        
        -- Mes
        JSON_OBJECT('month', DATE_FORMAT(vi.created_at, '%Y-%m'), 'arranques', COUNT(vi.status)) AS month_data,
        
        -- Año
        JSON_OBJECT('year', YEAR(vi.created_at), 'arranques', COUNT(vi.status)) AS year_data

    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
    AND vi.status = true
    AND vi.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)
    GROUP BY 
        DATE_FORMAT(vi.created_at, '%Y-%m-%d'), 
        CONCAT(YEAR(vi.created_at), '-', WEEK(vi.created_at)),
        DATE_FORMAT(vi.created_at, '%Y-%m'),
        YEAR(vi.created_at)
    ORDER BY DATE_FORMAT(vi.created_at, '%Y-%m-%d') DESC
""", nativeQuery = true)
    List<Map<String, Object>> countIgnitions(@Param("vehicleId") Long vehicleId);

    @Query(value = """
    SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS date, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)
      AND vi.status = true
    GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
    ORDER BY date DESC
    """, nativeQuery = true)
    List<Map<String, Object>> countIgnitionsByWeek(@Param("vehicleId") Long vehicleId);
}
