package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.ChecklistTypeModel;
import com.icm.telemetria_peru_api.services.ChecklistTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/checklist-types")
@RequiredArgsConstructor
public class ChecklistTypeController {
    private final ChecklistTypeService checklistTypeService;

    @GetMapping
    public List<ChecklistTypeModel> findAll(){
        return checklistTypeService.findAll();
    }

    @GetMapping("/paged")
    public Page<ChecklistTypeModel> findAll(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return checklistTypeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistTypeModel> finById(@PathVariable Long id) {
        try {
            ChecklistTypeModel checklistType = checklistTypeService.findById(id);
            return new ResponseEntity<>(checklistType, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ChecklistTypeModel checklistTypeModel){
        try {
            ChecklistTypeModel checklistType = checklistTypeService.save(checklistTypeModel);
            return new ResponseEntity<>(checklistType, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ChecklistTypeModel checklistTypeModel, @PathVariable Long id){
        try {
            ChecklistTypeModel checklistType = checklistTypeService.update(checklistTypeModel, id);
            return new ResponseEntity<>(checklistType, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            checklistTypeService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
