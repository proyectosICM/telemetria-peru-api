package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FuelEfficiencyRepository extends JpaRepository<FuelEfficiencyModel, Long> {
    List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId);

    Page<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    FuelEfficiencyModel findTopByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);

    List<FuelEfficiencyModel> findByFuelEfficiencyStatusNot(FuelEfficiencyStatus status);


    /**
     * STATS
     */
    @Query(value = """
                WITH RECURSIVE dates AS (
                    SELECT DATE_FORMAT(DATE(CONCAT(:year, '-', :month, '-01')), '%Y-%m-%d') AS day
                    UNION ALL
                    SELECT DATE_ADD(day, INTERVAL 1 DAY)
                    FROM dates
                    WHERE day < LAST_DAY(CONCAT(:year, '-', :month, '-01'))
                )
                SELECT 
                    d.day AS day,
                    IFNULL(AVG(CASE WHEN fe.fuel_efficiency > 0 THEN fe.fuel_efficiency ELSE NULL END), 0) AS avgkm,
                    IFNULL(AVG(CASE WHEN fe.fuel_consumption_per_hour > 0 THEN fe.fuel_consumption_per_hour ELSE NULL END), 0) AS avgh
                FROM dates d
                LEFT JOIN fuel_efficiency fe 
                    ON DATE(CONVERT_TZ(fe.created_at, '+00:00', '-05:00')) = d.day
                    AND MONTH(CONVERT_TZ(fe.created_at, '+00:00', '-05:00')) = :month
                    AND YEAR(CONVERT_TZ(fe.created_at, '+00:00', '-05:00')) = :year
                    AND fe.vehicle_id = :vehicleId
                GROUP BY d.day
                ORDER BY d.day
            """, nativeQuery = true)
    List<Map<String, Object>> findDailyAveragesForMonth(@Param("vehicleId") Long vehicleId, @Param("month") Integer month, @Param("year") Integer year);

    @Query(value = """
                SELECT 
                    CONCAT(:year, '-', months.month) AS month,
                    IFNULL(AVG(CASE WHEN fe.fuel_efficiency > 0 THEN fe.fuel_efficiency ELSE NULL END), 0) AS avgkm,
                    IFNULL(AVG(CASE WHEN fe.fuel_consumption_per_hour > 0 THEN fe.fuel_consumption_per_hour ELSE NULL END), 0) AS avgh
                FROM (
                    SELECT '01' AS month UNION ALL
                    SELECT '02' UNION ALL
                    SELECT '03' UNION ALL
                    SELECT '04' UNION ALL
                    SELECT '05' UNION ALL
                    SELECT '06' UNION ALL
                    SELECT '07' UNION ALL
                    SELECT '08' UNION ALL
                    SELECT '09' UNION ALL
                    SELECT '10' UNION ALL
                    SELECT '11' UNION ALL
                    SELECT '12'
                ) AS months
                LEFT JOIN fuel_efficiency fe
                    ON DATE_FORMAT(CONVERT_TZ(fe.created_at, '+00:00', '-05:00'), '%Y-%m') = CONCAT(:year, '-', months.month)
                    AND fe.vehicle_id = :vehicleId
                    AND fe.status = :status
                GROUP BY months.month
                ORDER BY months.month
            """, nativeQuery = true)
    List<Map<String, Object>> findMonthlyAveragesForYear(@Param("vehicleId") Long vehicleId, @Param("status") String status, @Param("year") Integer year);

    @Query(value = """
                           SELECT 
                               fe.status AS status,
                               SUM(fe.accumulated_hours) AS totalHours,
                               SUM(fe.initial_fuel - fe.final_fuel) AS totalFuelConsumed,
                               AVG(fe.fuel_consumption_per_hour) AS avgFuelEfficiency
                           FROM fuel_efficiency fe
                           WHERE fe.vehicle_id = :vehicleId
                           AND (
                               (:year IS NOT NULL AND YEAR(fe.start_time) = :year) OR
                               (:month IS NOT NULL AND YEAR(fe.start_time) = :year AND MONTH(fe.start_time) = :month) OR
                               (:day IS NOT NULL AND DAY(fe.start_time) = :day AND MONTH(fe.start_time) = :month AND YEAR(fe.start_time) = :year)
                           )
                           GROUP BY fe.status
                       """, nativeQuery = true)
    List<Object[]> getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(@Param("vehicleId") Long vehicleId, @Param("year") Integer year, @Param("month") Integer month, @Param("day") Integer day);
}
