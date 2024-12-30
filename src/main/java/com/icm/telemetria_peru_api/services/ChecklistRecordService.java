package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.repositories.ChecklistRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChecklistRecordService {
    @Autowired
    private ChecklistRecordRepository checklistRecordRepository;

    // Objeto para convertir el Map en un archivo JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // En el servicio
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

    public ChecklistRecordModel findById(Long id){
        return checklistRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
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


    public ChecklistRecordModel saveWithJson(ChecklistRecordModel checklistRecordModel, Map<String, Object> jsonData) throws IOException {
        ChecklistRecordModel savedChecklistRecordModel = checklistRecordRepository.save(checklistRecordModel);

        File directory = new File("CL");
        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("Carpeta 'CL' creada.");
        }

        String fileName = "CL/" + savedChecklistRecordModel.getId() + ".json";
        try (FileWriter file = new FileWriter(fileName)) {
            objectMapper.writeValue(file, jsonData);
            //System.out.println("Archivo JSON guardado en: " + fileName);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo JSON: " + e.getMessage());
            throw e;
        }

        return savedChecklistRecordModel;
    }



    public void deleteById(Long id){
        checklistRecordRepository.deleteById(id);
    }
}
