package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GasRecordsRepository extends JpaRepository<GasRecordModel, Long> {
}
