package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRecordRepository extends JpaRepository<BatteryRecordModel, Long> {
    List<BatteryRecordModel> findByBatteryModelId(Long vehicleId);
    Page<BatteryRecordModel> findByBatteryModelId(Long vehicleId, Pageable pageable);

    /**
     * Deletes all BatteryRecordModel entries associated with the specified battery ID.
     * This operation is transactional, meaning that it will be executed within a transaction
     * context and can be rolled back if an error occurs during the execution.
     *
     * @param batteryId the ID of the battery whose records should be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BatteryRecordModel br WHERE br.batteryModel.id = :batteryId")
    void deleteByBatteryId(@Param("batteryId") Long batteryId);
}