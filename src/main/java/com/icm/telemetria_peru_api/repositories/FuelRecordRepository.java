package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface FuelRecordRepository extends JpaRepository<FuelRecordModel, Long> {
    List<FuelRecordModel> findByVehicleModelId(Long vehicleId);
    Page<FuelRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    @Query(value = """
        SELECT 
            DATE_FORMAT(fr.createdAt, '%Y-%m-%d %H:00:00') AS hour,
            AVG(fr.valueData) AS averageValue
        FROM fuel_records fr
        WHERE DATE(fr.created_at) = :date
        GROUP BY DATE_FORMAT(fr.created_at, '%Y-%m-%d %H:00:00')
        ORDER BY hour
    """, nativeQuery = true)
    List<Map<String, Object>> findHourlyAverageByDate(@Param("date") LocalDate date);
}
