package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.AlarmRecordModel;
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
public interface AlarmRecordRepository extends JpaRepository<AlarmRecordModel, Long> {
    List<AlarmRecordModel> findByVehicleModelId(Long vehicleId);
    Page<AlarmRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
