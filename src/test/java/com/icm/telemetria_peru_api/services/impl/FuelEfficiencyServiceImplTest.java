package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuelEfficiencyServiceImplTest {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    @Mock
    private FuelEfficiencyRepository fuelEfficiencyRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Spy
    @InjectMocks
    private FuelEfficiencyServiceImpl service;

    @Test
    void addSecondsCapsTodayTotalToElapsedDayTime() {
        Long vehicleId = 5L;
        LocalDate day = LocalDate.of(2026, 3, 17);
        ZonedDateTime now = ZonedDateTime.of(2026, 3, 17, 1, 0, 0, 0, ZONE);

        VehicleModel vehicle = new VehicleModel();
        vehicle.setId(vehicleId);

        FuelEfficiencyModel row = new FuelEfficiencyModel();
        row.setVehicleModel(vehicle);
        row.setDay(day);
        row.setParkedSeconds(3500L);
        row.setIdleSeconds(0L);
        row.setOperationSeconds(0L);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(fuelEfficiencyRepository.findByVehicleModel_IdAndDay(vehicleId, day)).thenReturn(Optional.of(row));
        when(fuelEfficiencyRepository.save(row)).thenReturn(row);
        when(service.now()).thenReturn(now);

        service.addSeconds(vehicleId, day, 1000L, 0L, 0L);

        ArgumentCaptor<FuelEfficiencyModel> captor = ArgumentCaptor.forClass(FuelEfficiencyModel.class);
        verify(fuelEfficiencyRepository).save(captor.capture());

        FuelEfficiencyModel saved = captor.getValue();
        assertEquals(3600L, saved.getParkedSeconds());
        assertEquals(0L, saved.getIdleSeconds());
        assertEquals(0L, saved.getOperationSeconds());
    }
}
