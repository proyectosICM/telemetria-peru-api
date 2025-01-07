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
    SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.status = true
      AND DATE(vi.created_at) = CURRENT_DATE
    GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
""", nativeQuery = true)
    List<Map<String, Object>> countsDay(@Param("vehicleId") Long vehicleId);

    @Query(value = """ 
    SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.status = true
      AND vi.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)  
    GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
    ORDER BY day DESC  
""", nativeQuery = true)
    List<Map<String, Object>> countsWeek(@Param("vehicleId") Long vehicleId);

    @Query(value = """ 
    SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.status = true
      AND MONTH(vi.created_at) = MONTH(CURRENT_DATE)  
      AND YEAR(vi.created_at) = YEAR(CURRENT_DATE)   
    GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
    ORDER BY day DESC  
""", nativeQuery = true)
    List<Map<String, Object>> countsMonth(@Param("vehicleId") Long vehicleId);

    @Query(value = """ 
    SELECT YEAR(vi.created_at) AS year, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.status = true
      AND YEAR(vi.created_at) = YEAR(CURRENT_DATE)
    GROUP BY YEAR(vi.created_at)
    ORDER BY year DESC  
""", nativeQuery = true)
    List<Map<String, Object>> countsYear(@Param("vehicleId") Long vehicleId);

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
