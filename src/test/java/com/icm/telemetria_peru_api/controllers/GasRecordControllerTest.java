package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.services.GasRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class GasRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GasRecordService gasRecordService;
/*
    @Test
    void testFindByIdSuccess() throws Exception {
        Long id = 1L;

        // Mock del modelo VehicleModel
        VehicleModel vehicleMock = new VehicleModel();
        vehicleMock.setId(1L);
        vehicleMock.setLicensePlate("ABC-123");
        vehicleMock.setFuelType(FuelType.DIESEL);


        // Mock del modelo GasChangeModel
        GasChangeModel mockRecord = new GasChangeModel(
                id,
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                10.0,
                ZonedDateTime.now(),
                15.0,
                vehicleMock,
                ZonedDateTime.now(),
                ZonedDateTime.now()
        );

        // Mock del servicio
        when(gasRecordService.findById(id)).thenReturn(Optional.of(mockRecord));

        // Ejecución y validación de la prueba
        mockMvc.perform(MockMvcRequestBuilders.get("/api/gas-records/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.pressureBeforeChange").value(10.0))
                .andExpect(jsonPath("$.pressureAfterChange").value(15.0));

        // Verificación de que el servicio fue llamado
        Mockito.verify(gasRecordService).findById(id);
    }*/
}

/*

    @Test
    void testFindByIdNotFound() throws Exception {
        // Simular el comportamiento cuando no se encuentra el registro
        Long id = 999L;
        Mockito.when(gasRecordService.findById(id)).thenReturn(Optional.empty());

        // Ejecutar la petición GET
        mockMvc.perform(MockMvcRequestBuilders.get("/api/gas-records/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Sigue devolviendo 200 con Optional.empty()
                .andExpect(jsonPath("$").doesNotExist());

        // Verificar que el servicio fue llamado
        Mockito.verify(gasRecordService).findById(id);
    }

    @Test
    void testFindByIdServerError() throws Exception {
        // Simular un error interno en el servicio
        Long id = 1L;
        Mockito.when(gasRecordService.findById(id)).thenThrow(new RuntimeException("Internal Server Error"));

        // Ejecutar la petición GET
        mockMvc.perform(MockMvcRequestBuilders.get("/api/gas-records/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Verificar status 500
                .andExpect(jsonPath("$").doesNotExist());

        // Verificar que el servicio fue llamado
        Mockito.verify(gasRecordService).findById(id);
    }
 */