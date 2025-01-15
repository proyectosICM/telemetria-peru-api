package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.ChecklistRecordDTO;
import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.services.ChecklistRecordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/checklist-records")
@RequiredArgsConstructor
public class ChecklistRecordController {
    private final ChecklistRecordService checklistRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistRecordModel> findById(@PathVariable Long id) {
        try {
            ChecklistRecordModel data = checklistRecordService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the content of a JSON file associated with a specific ChecklistRecord.
     *
     * This endpoint fetches the content of a JSON file linked to a given ChecklistRecord ID.
     * If the record or file is not found, it returns an appropriate HTTP status code.
     *
     * @param id The ID of the ChecklistRecord whose JSON file content is being retrieved.
     * @return A ResponseEntity containing the JSON file content as a String if successful,
     *         or an error response with the appropriate HTTP status code.
     * @throws EntityNotFoundException If no ChecklistRecord is found with the given ID.
     * @throws IOException If an error occurs while reading the JSON file.
     */
    @GetMapping("/json/{id}")
    public ResponseEntity<?> getJsonFile(@PathVariable Long id) {
        try {
            String jsonFilePath = checklistRecordService.getJsonFileContentById(id);
            return ResponseEntity.ok().body(jsonFilePath);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error getting JSON file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<ChecklistRecordModel> findAll(){
        return checklistRecordService.findAll();
    }

    @GetMapping("/paged")
    public Page<ChecklistRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return checklistRecordService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{id}")
    public ResponseEntity<List<ChecklistRecordModel>> findByVehicle(@PathVariable Long id){
        try {
            List<ChecklistRecordModel> data = checklistRecordService.findByVehicleModelId(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-vehicle-paged/{id}")
    public ResponseEntity<Page<ChecklistRecordModel>> findByVehicle(@PathVariable Long id,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "8") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ChecklistRecordModel> data = checklistRecordService.findByVehicleModelId(id, pageable );
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-company/{id}")
    public ResponseEntity<List<ChecklistRecordModel>> findByCompany(@PathVariable Long id){
        try {
            List<ChecklistRecordModel> data = checklistRecordService.findByCompanyModelId(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-company-paged/{id}")
    public ResponseEntity<Page<ChecklistRecordModel>> findByCompany(@PathVariable Long id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "8") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ChecklistRecordModel> data = checklistRecordService.findByCompanyModelId(id, pageable );
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the latest ChecklistRecord for a specific vehicle on the current day.
     *
     * This endpoint fetches the most recent checklist record for a given vehicle ID.
     * If no record is found, it returns a 404 Not Found status with an appropriate error message.
     *
     * @param vehicleId The ID of the vehicle whose latest checklist is being retrieved.
     * @return A ResponseEntity containing the latest ChecklistRecordModel if found, or an error message if not.
     */
    @GetMapping("/latest/{vehicleId}")
    public ResponseEntity<ChecklistRecordModel> getLatestChecklistForVehicle(@PathVariable Long vehicleId) {
        try {
            ChecklistRecordModel checklistRecord = checklistRecordService.getLatestChecklistForVehicle(vehicleId);
            return ResponseEntity.ok(checklistRecord);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Saves a ChecklistRecord along with its associated JSON data.
     *
     * This endpoint receives a DTO containing a ChecklistRecordModel and a JSON payload.
     * It saves the record to the database and writes the JSON data to a file. If the operation
     * is successful, it returns the saved ChecklistRecordModel. If an error occurs, appropriate
     * HTTP status codes and messages are returned.
     *
     * @param checklistRecordDTO A DTO containing the ChecklistRecordModel and its associated JSON data.
     * @return A ResponseEntity containing the saved ChecklistRecordModel if successful, or an error response if not.
     * @throws EntityNotFoundException If a related entity is not found during the save operation.
     * @throws IOException If an error occurs while saving the JSON file.
     */
    @PostMapping
    public ResponseEntity<ChecklistRecordModel> save(@RequestBody ChecklistRecordDTO checklistRecordDTO) {
        try {
            ChecklistRecordModel checklistRecordModel = checklistRecordDTO.getChecklistRecordModel();
            Map<String, Object> jsonData = checklistRecordDTO.getJsonData();

            ChecklistRecordModel data = checklistRecordService.saveWithJson(checklistRecordModel, jsonData);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try {
            checklistRecordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
