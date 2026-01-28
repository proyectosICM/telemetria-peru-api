package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FuelRecordRepository extends JpaRepository<FuelRecordModel, Long> {

    @Query(value = """
            SELECT 
                DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d %H:00:00') AS hour,
                AVG(fr.value_data) AS averageValue
            FROM fuel_records fr
            WHERE DATE(CONVERT_TZ(fr.created_at, '+00:00', '-05:00')) = :date 
            AND fr.vehicle_id = :vehicleId
            GROUP BY DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d %H:00:00')
            ORDER BY hour
            """, nativeQuery = true)
    List<Map<String, Object>> findHourlyAverageByDate(@Param("date") LocalDate date, @Param("vehicleId") Long vehicleId);


    @Query(value = """
            SELECT 
                DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d') AS day,
                AVG(fr.value_data) AS averageValue
            FROM fuel_records fr
            WHERE DATE(CONVERT_TZ(fr.created_at, '+00:00', '-05:00')) BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE()
            AND fr.vehicle_id = :vehicleId
            GROUP BY DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d')
            ORDER BY day
            """, nativeQuery = true)
    List<Map<String, Object>> findDailyAveragesForLast7Days(@Param("vehicleId") Long vehicleId);

    @Query(value = """
            SELECT 
                DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d') AS day,
                AVG(fr.value_data) AS averageValue
            FROM fuel_records fr
            WHERE DATE(CONVERT_TZ(fr.created_at, '+00:00', '-05:00')) BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND CURDATE()
            AND fr.vehicle_id = :vehicleId
            GROUP BY DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m-%d')
            ORDER BY day
            """, nativeQuery = true)
    List<Map<String, Object>> findDailyAveragesForCurrentMonth(@Param("vehicleId") Long vehicleId);

    @Query(value = """
            SELECT 
                DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m') AS month,
                AVG(fr.value_data) AS averageValue
            FROM fuel_records fr
            WHERE YEAR(CONVERT_TZ(fr.created_at, '+00:00', '-05:00')) = YEAR(CURDATE())
                AND fr.vehicle_id = :vehicleId
            GROUP BY DATE_FORMAT(CONVERT_TZ(fr.created_at, '+00:00', '-05:00'), '%Y-%m')
            ORDER BY month
            """, nativeQuery = true)
    List<Map<String, Object>> findMonthlyAveragesForCurrentYear(@Param("vehicleId") Long vehicleId);

    List<FuelRecordModel> findByVehicleModelId(Long  vehicleId);

    Page<FuelRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);


    List<FuelRecordModel> findByVehicleModelIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long vehicleId,
            ZonedDateTime start,
            ZonedDateTime end
    );

    long countByVehicleModelId(Long vehicleId);

    // Últimos 10 registros del vehículo (por createdAt)
    List<FuelRecordModel> findTop10ByVehicleModel_IdOrderByCreatedAtDesc(Long vehicleId);

    // Vehículos con lecturas recientes (evita recorrer todos)
    @Query("""
        select distinct fr.vehicleModel.id
        from FuelRecordModel fr
        where fr.createdAt >= :since
    """)
    List<Long> findActiveVehicleIdsSince(@Param("since") ZonedDateTime since);
}
