package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.dto.IgnitionCountByDate;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleIgnitionRepository extends JpaRepository<VehicleIgnitionModel, Long> {
    List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    VehicleIgnitionModel findTopByVehicleModelOrderByCreatedAtDesc(VehicleModel  vehicleModel);
    List<VehicleIgnitionModel> findByVehicleModelIdOrderByCreatedAt(Long vehicleId);

    @Query("SELECT new com.icm.telemetria_peru_api.dto.IgnitionCountByDate(" +
            "CAST(FUNCTION('DATE', vi.createdAt) AS LocalDate), COUNT(vi)) " +
            "FROM VehicleIgnitionModel vi " +
            "WHERE vi.vehicleModel.id = :vehicleId " +
            "AND vi.createdAt >= CURRENT_DATE - 7 " +
            "GROUP BY FUNCTION('DATE', vi.createdAt) " +
            "ORDER BY FUNCTION('DATE', vi.createdAt) DESC")
    List<IgnitionCountByDate> countIgnitionsByWeek2(Long vehicleId);

    @Query(value = """
    SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d %H:00:00') AS date, 
           COUNT(vi.status) AS count 
    FROM vehicle_ignition vi 
    WHERE vi.vehicle_id = :vehicleId
      AND vi.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)
    GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d %H:00:00')
    ORDER BY date DESC
    """, nativeQuery = true)
    List<IgnitionCountByDate> countIgnitionsByWeek(@Param("vehicleId") Long vehicleId);
}
