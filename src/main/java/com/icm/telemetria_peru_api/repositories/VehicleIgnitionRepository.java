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


    @Query(value = """
    SELECT 
        DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day,
        CONCAT(YEAR(vi.created_at), '-', WEEK(vi.created_at)) AS week,
        DATE_FORMAT(vi.created_at, '%Y-%m') AS month,
        YEAR(vi.created_at) AS year,
        COUNT(vi.status) AS arranques
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.status = true
      AND vi.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH) -- Para solo contar los datos del mes actual
    GROUP BY 
        DATE_FORMAT(vi.created_at, '%Y-%m-%d'), 
        CONCAT(YEAR(vi.created_at), '-', WEEK(vi.created_at)),
        DATE_FORMAT(vi.created_at, '%Y-%m'),
        YEAR(vi.created_at)
    ORDER BY vi.created_at DESC
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
