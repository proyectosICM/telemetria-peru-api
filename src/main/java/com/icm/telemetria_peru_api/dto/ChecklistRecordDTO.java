package com.icm.telemetria_peru_api.dto;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import lombok.Data;

import java.util.Map;
@Data
public class ChecklistRecordDTO {
    private ChecklistRecordModel checklistRecordModel;
    private Map<String, Object> jsonData;
}
