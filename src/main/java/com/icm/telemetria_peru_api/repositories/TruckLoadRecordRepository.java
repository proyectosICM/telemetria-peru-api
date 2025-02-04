package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
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
public interface TruckLoadRecordRepository extends JpaRepository<TruckLoadRecordModel, Long> {
    List<TruckLoadRecordModel> findByVehicleModelId(Long vehicleId);
    Page<TruckLoadRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    /** */
    @Query("SELECT COUNT(t) FROM TruckLoadRecordModel t WHERE t.vehicleModel.id = :vehicleId AND DATE(t.createdAt) = :date")
    long countByVehicleModelIdAndDate(@Param("vehicleId") Long vehicleId, @Param("date") LocalDate date);

    List<TruckLoadRecordModel> findByVehicleModelIdAndCreatedAtBetween(Long vehicleModelId, ZonedDateTime startTimestamp, ZonedDateTime endTimestamp);

    /** */

    @Query("SELECT new map(DATE(t.createdAt) as day, COUNT(t) as count) " +
            "FROM TruckLoadRecordModel t " +
            "WHERE t.vehicleModel.id = :vehicleId " +
            "GROUP BY DATE(t.createdAt) " +
            "ORDER BY DATE(t.createdAt)")
    Page<Map<String, Object>> findDailyRecordCountsByVehicle(@Param("vehicleId") Long vehicleId, Pageable pageable);
}