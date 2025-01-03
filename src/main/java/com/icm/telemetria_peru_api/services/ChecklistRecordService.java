package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.repositories.ChecklistRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ChecklistRecordService {
    private final ChecklistRecordRepository checklistRecordRepository;
    private final MqttMessagePublisher mqttMessagePublisher;

    // Object to convert the Map to a JSON file
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChecklistRecordModel findById(Long id){
        return checklistRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    /**
     * Retrieves the content of the JSON file associated with a ChecklistRecord by its ID.
     *
     * @param id The ID of the ChecklistRecord.
     * @return The content of the JSON file as a String.
     * @throws IOException If an error occurs while reading the file.
     * @throws EntityNotFoundException If no ChecklistRecord is found with the given ID.
     */
    public String getJsonFileContentById(Long id) throws IOException {
        ChecklistRecordModel checklistRecordModel = findById(id);
        String filePath = "CL/" + checklistRecordModel.getId() + ".json";

        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public List<ChecklistRecordModel> findAll(){
        return checklistRecordRepository.findAll();
    }

    public Page<ChecklistRecordModel> findAll(Pageable pageable){
        return checklistRecordRepository.findAll(pageable);
    }

    public List<ChecklistRecordModel> findByVehicleModelId(Long vehicleId){
        return checklistRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<ChecklistRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return checklistRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public List<ChecklistRecordModel> findByCompanyModelId(Long companyId){
        return checklistRecordRepository.findByCompanyModelId(companyId);
    }

    public Page<ChecklistRecordModel> findByCompanyModelId(Long companyId, Pageable pageable){
        return checklistRecordRepository.findByCompanyModelId(companyId, pageable);
    }

    /**
     * Retrieves the latest ChecklistRecord for a specific vehicle on the current day.
     *
     * This method checks for the most recent checklist record of a vehicle based on the current date.
     * If no record is found, it triggers an MQTT message indicating the absence of the checklist
     * and throws a RuntimeException.
     *
     * @param vehicleId The ID of the vehicle whose checklist is being queried.
     * @return The latest ChecklistRecordModel for the vehicle on the current day.
     * @throws RuntimeException If no checklist record is found for the vehicle on the current day.
     */
    public ChecklistRecordModel getLatestChecklistForVehicle(Long vehicleId) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Optional<ChecklistRecordModel> optionalChecklist = checklistRecordRepository.findLatestByVehicleIdAndDay(vehicleId, startOfDay, endOfDay);

        if (optionalChecklist.isEmpty()) {
            mqttMessagePublisher.CheckListShutDown(vehicleId);

            throw new RuntimeException("No checklist record found for today for vehicle ID: " + vehicleId);
        }

        return optionalChecklist.get();
    }


    /**
     * Saves a ChecklistRecord along with its associated JSON data to a file.
     *
     * This method saves a ChecklistRecordModel instance to the database and then writes the
     * provided JSON data to a file named after the ID of the saved record. The JSON file is
     * stored in the "CL" directory, which is created if it does not already exist.
     *
     * @param checklistRecordModel The ChecklistRecordModel to be saved.
     * @param jsonData A Map containing the JSON data to be saved to a file.
     * @return The saved ChecklistRecordModel instance.
     * @throws IOException If an error occurs while writing the JSON file to the filesystem.
     */
    public ChecklistRecordModel saveWithJson(ChecklistRecordModel checklistRecordModel, Map<String, Object> jsonData) throws IOException {
        ChecklistRecordModel savedChecklistRecordModel = checklistRecordRepository.save(checklistRecordModel);

        File directory = new File("CL");
        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("'CL' Folder created.");
        }

        String fileName = "CL/" + savedChecklistRecordModel.getId() + ".json";
        try (FileWriter file = new FileWriter(fileName)) {
            objectMapper.writeValue(file, jsonData);
            //System.out.println("JSON file saved in: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
            throw e;
        }

        return savedChecklistRecordModel;
    }

    public void deleteById(Long id){
        checklistRecordRepository.deleteById(id);
    }
}
