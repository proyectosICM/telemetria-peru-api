package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

public interface FuelTheftAlertService {

    FuelTheftAlertModel getById(Long id);

    Page<FuelTheftAlertModel> search(
            Long vehicleId,
            String status,
            ZonedDateTime start,
            ZonedDateTime end,
            Pageable pageable
    );
}
