package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.dto.FuelReportSummaryDTO;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleFuelReportRepositpory extends JpaRepository<VehicleFuelReportModel, Long> {
    Optional<VehicleFuelReportModel> findTopByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);

    List<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    @Query(value = """
    SELECT 
        AVG(initial_fuel - final_fuel) AS averageFuelConsumption,
        SUM(EXTRACT(EPOCH FROM idle_time)) AS totalIdleTime,
        SUM(EXTRACT(EPOCH FROM parked_time)) AS totalParkedTime,
        SUM(EXTRACT(EPOCH FROM operating_time)) AS totalOperatingTime
    FROM vehicle_fuel_report
    WHERE (:vehicleId IS NULL OR vehicle_model_id = :vehicleId)
      AND (:year IS NULL OR EXTRACT(YEAR FROM date) = :year)
      AND (:month IS NULL OR EXTRACT(MONTH FROM date) = :month)
      AND (:day IS NULL OR EXTRACT(DAY FROM date) = :day)
""", nativeQuery = true)
    Object[] findFuelReportSummaryRaw(
            @Param("vehicleId") Long vehicleId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("day") Integer day
    );

}
