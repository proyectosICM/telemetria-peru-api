package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import com.icm.telemetria_peru_api.repositories.FuelTheftAlertRepository;
import com.icm.telemetria_peru_api.services.FuelTheftAlertService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class FuelTheftAlertServiceImpl implements FuelTheftAlertService {

    private final FuelTheftAlertRepository repository;

    @Override
    public FuelTheftAlertModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FuelTheftAlert not found: " + id));
    }

    @Override
    public Page<FuelTheftAlertModel> search(
            Long vehicleId,
            String status,
            ZonedDateTime start,
            ZonedDateTime end,
            Pageable pageable
    ) {
        return repository.search(vehicleId, status, start, end, pageable);
    }
}
