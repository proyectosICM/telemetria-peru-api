package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistRecordRepository extends JpaRepository<ChecklistRecordModel, Long> {
    List<ChecklistRecordModel> findByVehicleModelId(Long vehicleId);
    Page<ChecklistRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    List<ChecklistRecordModel> findByCompanyModelId(Long companyId);
    Page<ChecklistRecordModel> findByCompanyModelId(Long companyId, Pageable pageable);

    @Query("SELECT c FROM ChecklistRecordModel c " +
            "WHERE c.vehicleModel.id = :vehicleId " +
            "AND c.createdAt >= :startOfDay " +
            "AND c.createdAt <= :endOfDay " +
            "ORDER BY c.createdAt DESC")
    Optional<ChecklistRecordModel> findLatestByVehicleIdAndDay(
            @Param("vehicleId") Long vehicleId,
            @Param("startOfDay") ZonedDateTime startOfDay,
            @Param("endOfDay") ZonedDateTime endOfDay
    );
}
