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

    @Query("""
        SELECT 
            AVG(v.initialFuel - v.finalFuel) AS averageFuelConsumption,
            SUM(v.idleTime) AS totalIdleTime,
            SUM(v.parkedTime) AS totalParkedTime,
            SUM(v.operatingTime) AS totalOperatingTime
        FROM VehicleFuelReportModel v
        WHERE (:vehicleId IS NULL OR v.vehicleModel.id = :vehicleId)
          AND (:year IS NULL OR FUNCTION('YEAR', v.date) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', v.date) = :month)
          AND (:day IS NULL OR FUNCTION('DAY', v.date) = :day)
    """)
    FuelReportSummaryDTO findFuelReportSummary(
            @Param("vehicleId") Long vehicleId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("day") Integer day
    );

}
