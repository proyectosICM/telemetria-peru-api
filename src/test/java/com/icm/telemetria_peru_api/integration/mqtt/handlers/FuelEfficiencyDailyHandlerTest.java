package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleStateCurrentModel;
import com.icm.telemetria_peru_api.repositories.VehicleStateCurrentRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuelEfficiencyDailyHandlerTest {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    @Mock
    private FuelEfficiencyService fuelEfficiencyService;

    @Mock
    private VehicleStateCurrentRepository vehicleStateCurrentRepository;

    @Spy
    @InjectMocks
    private FuelEfficiencyDailyHandler handler;

    @Test
    void processClampsFutureEventTimeToNow() {
        Long vehicleId = 99L;
        ZonedDateTime now = ZonedDateTime.of(2026, 3, 17, 1, 0, 0, 0, ZONE);

        VehicleStateCurrentModel current = new VehicleStateCurrentModel();
        current.setVehicleModel(new VehicleModel());
        current.setStatus(FuelEfficiencyStatus.ESTACIONADO);
        current.setLastEventTime(ZonedDateTime.of(2026, 3, 17, 0, 0, 0, 0, ZONE));

        VehiclePayloadMqttDTO payload = new VehiclePayloadMqttDTO();
        payload.setTimestamp(String.valueOf(ZonedDateTime.of(2026, 3, 17, 5, 0, 0, 0, ZONE).toInstant().toEpochMilli()));
        payload.setIgnitionInfo(false);

        when(vehicleStateCurrentRepository.findByVehicleModel_Id(vehicleId)).thenReturn(Optional.of(current));
        when(handler.now()).thenReturn(now);

        handler.process(vehicleId, payload);

        verify(fuelEfficiencyService).addSeconds(vehicleId, now.toLocalDate(), 3600L, 0L, 0L);

        ArgumentCaptor<VehicleStateCurrentModel> captor = ArgumentCaptor.forClass(VehicleStateCurrentModel.class);
        verify(vehicleStateCurrentRepository).save(captor.capture());
        assertEquals(now, captor.getValue().getLastEventTime());
    }
}
