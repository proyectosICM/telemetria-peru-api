package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelEfficiencyQueueModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuelEfficiencyQueueRepository extends JpaRepository<FuelEfficiencyQueueModel, Long> {

    List<FuelEfficiencyQueueModel> findTop100ByProcessedFalseAndProcessingFalseOrderByCreatedAtAsc();

    List<FuelEfficiencyQueueModel> findTop100ByProcessedFalseOrderByCreatedAtAsc();

    long countByProcessedFalseAndProcessingFalse();

    long countByProcessedFalse();

    List<FuelEfficiencyQueueModel> findByProcessedFalseAndProcessingTrueOrderByCreatedAtAsc();

    void deleteByProcessedTrue();
}
