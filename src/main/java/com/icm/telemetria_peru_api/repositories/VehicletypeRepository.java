package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicletypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicletypeRepository extends JpaRepository<VehicletypeModel, Long> {
}
