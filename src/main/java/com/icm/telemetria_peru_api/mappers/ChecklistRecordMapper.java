package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.ChecklistRecordDTO;
import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ChecklistRecordMapper {
    public ChecklistRecordDTO mapToDTO(ChecklistRecordModel checklistRecordModel){
        Long id = checklistRecordModel.getId();
        String name = checklistRecordModel.getName();
        String fileName = checklistRecordModel.getFileName();
        int timer = checklistRecordModel.getTimer();
        Long driverId = checklistRecordModel.getDriverModel().getId();
        String driverName = checklistRecordModel.getDriverModel().getName() + " " + checklistRecordModel.getDriverModel().getLastName();
        Long vehicleId = checklistRecordModel.getVehicleModel().getId();
        String licensePlate = checklistRecordModel.getVehicleModel().getLicensePlate();
        Long type = checklistRecordModel.getChecklistTypeModel().getId();
        Long companyId = checklistRecordModel.getCompanyModel().getId();
        ZonedDateTime createdAt = checklistRecordModel.getCreatedAt();

        return new ChecklistRecordDTO(id, name, fileName, timer, driverId, driverName, vehicleId, licensePlate, type, companyId, createdAt);
    }
}
