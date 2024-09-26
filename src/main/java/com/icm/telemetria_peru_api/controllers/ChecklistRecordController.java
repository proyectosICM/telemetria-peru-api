package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.ChecklistRecordDTO;
import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.services.ChecklistRecordService;
import com.icm.telemetria_peru_api.services.ChecklistTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/checklist-records")
public class ChecklistRecordController {
    @Autowired
    private ChecklistRecordService checklistRecordService;
    @GetMapping
    public List<ChecklistRecordModel> findAll(){
        return checklistRecordService.findAll();
    }

    @GetMapping("/json/{id}")
    public ResponseEntity<?> getJsonFile(@PathVariable Long id) {
        try {
            String jsonFilePath = checklistRecordService.getJsonFileContentById(id);
            return ResponseEntity.ok().body(jsonFilePath);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error al obtener el archivo JSON: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/paged")
    public Page<ChecklistRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return checklistRecordService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistRecordModel> findById(@PathVariable Long id) {
        try {
            ChecklistRecordModel data = checklistRecordService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByVehicle/{id}")
    public ResponseEntity<?> findByVehicle(@PathVariable Long id){
        try {
            List<ChecklistRecordModel> data = checklistRecordService.findByVehicleModelId(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByVehicle-paged/{id}")
    public ResponseEntity<?> findByVehicle(@PathVariable Long id,
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

    @GetMapping("/findByCompany/{id}")
    public ResponseEntity<?> findByCompany(@PathVariable Long id){
        try {
            List<ChecklistRecordModel> data = checklistRecordService.findByCompanyModelId(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByCompany-paged/{id}")
    public ResponseEntity<?> findByCompany(@PathVariable Long id,
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

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ChecklistRecordDTO checklistRecordDTO) {
        try {
            ChecklistRecordModel checklistRecordModel = checklistRecordDTO.getChecklistRecordModel();
            Map<String, Object> jsonData = checklistRecordDTO.getJsonData();

            // Llamamos al servicio para guardar tanto el ChecklistRecordModel como el JSON.
            ChecklistRecordModel data = checklistRecordService.saveWithJson(checklistRecordModel, jsonData);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error al guardar el archivo JSON: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            checklistRecordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
