package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.services.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyModel> findById(@PathVariable Long companyId) {
        return companyService.findById(companyId)
                .map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Retrieves companies, as a list and paginated. */
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

    /** Retrieves companies by status, as a list and paginated. */
    @GetMapping("/findByStatus")
    public ResponseEntity<List<CompanyModel>> findByStatus(@RequestParam Boolean status){
        List<CompanyModel> dataModel = companyService.findByStatus(status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByStatus-page")
    public ResponseEntity<Page<CompanyModel>> findByStatusPage(@RequestParam Boolean status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyModel> dataModel = companyService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<CompanyModel> save(@RequestBody @Valid CompanyModel companyModel){
        CompanyModel dataModel = companyService.save(companyModel);
        return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> update(@PathVariable Long companyId,@RequestBody @Valid CompanyModel companyModel){
        try {
            CompanyModel dataModel = companyService.update(companyId, companyModel);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/changeStatus/{companyId}")
    public ResponseEntity<?> changeStatus(@PathVariable Long companyId){
        try {
            CompanyModel dataModel = companyService.changeStatus(companyId);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
