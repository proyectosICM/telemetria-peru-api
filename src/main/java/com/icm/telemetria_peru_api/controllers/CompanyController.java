package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.services.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("api/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping
    public List<CompanyModel> findAll() {
        return companyService.findAll();
    }
    @GetMapping("/page")
    public Page<CompanyModel> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return companyService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyModel> findById(@PathVariable @NotNull Long id) {
        try {
            CompanyModel data = companyService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByStatus")
    public ResponseEntity<?> findByStatus(@RequestParam @NotNull Boolean status){
        try{
            List<CompanyModel> data = companyService.findByStatus(status);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByStatus-page")
    public ResponseEntity<?> findByStatusPage(@RequestParam @NotNull Boolean status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CompanyModel> dataModel = companyService.findByStatus(status, pageable);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid CompanyModel companyModel){
        try {
            CompanyModel dataModel = companyService.save(companyModel);
            return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> update(@PathVariable @NotNull Long companyId,@RequestBody @Valid CompanyModel companyModel){
        try {
            CompanyModel dataModel = companyService.update(companyId, companyModel);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/changeStatus/{companyId}")
    public ResponseEntity<?> changeStatus(@PathVariable @NotNull Long companyId){
        try {
            CompanyModel dataModel = companyService.changeStatus(companyId);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> delete(@PathVariable @NotNull Long companyId){
        companyService.deleteById(companyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
