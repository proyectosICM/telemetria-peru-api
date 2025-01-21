package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.services.DriverService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverModel> findById(@PathVariable @NotNull Long driverId) {
        return driverService.findById(driverId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Retrieves drivers, as a list and paginated. */
    @GetMapping
    public List<DriverModel> findAll() {
        return driverService.findAll();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<DriverModel>> findAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by status, as a list and paginated. */
    @GetMapping("/by-status")
    public ResponseEntity<List<DriverModel>> findByStatus(@RequestParam @NotNull Boolean status){
        List<DriverModel> dataModel = driverService.findByStatus(status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/by-status-paged")
    public ResponseEntity<Page<DriverModel>> findByStatusPage(@RequestParam @NotNull Boolean status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company, as a list and paginated. */
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<DriverModel>> findByCompanyModelId(@PathVariable @NotNull Long companyId){
        List<DriverModel> dataModel = driverService.findByCompanyModelId(companyId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/by-company-paged/{companyId}")
    public ResponseEntity<Page<DriverModel>> findByCompanyModelId(@PathVariable @NotNull Long companyId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company and status, as a list and paginated. */
    @GetMapping("/by-company-and-status/{companyId}")
    public ResponseEntity<List<DriverModel>> findByCompanyModelIdAndStatus(@PathVariable @NotNull Long companyId, @RequestParam Boolean status){
        List<DriverModel> dataModel = driverService.findByCompanyModelIdAndStatus(companyId, status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/by-company-and-status-paged/{companyId}")
    public ResponseEntity<Page<DriverModel>> findByCompanyModelIdAndStatus(@PathVariable @NotNull Long companyId,
                                                                  @RequestParam @NotNull Boolean status,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByCompanyModelIdAndStatus(companyId, status , pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid DriverModel driverModel){
        try {
            DriverModel dataModel = driverService.save(driverModel);
            return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**  Main data update */
    @PutMapping("/{driverId}")
    public ResponseEntity<DriverModel> updateMainData(@PathVariable @NotNull Long driverId, @RequestBody @Valid DriverModel driverModel){
        try {
            DriverModel dataModel = driverService.updateMainData(driverId, driverModel);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**  RFID update */
    @PutMapping("/update-RFID/{driverId}")
    public ResponseEntity<DriverModel> updateRFID(@PathVariable @NotNull Long driverId, @RequestBody @NotBlank String newRFId){
        try {
            DriverModel dataModel = driverService.updateRFID(driverId, newRFId);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**  status update */
    @PutMapping("/status-toggle/{driverId}")
    public ResponseEntity<DriverModel> statusToggle(@PathVariable @NotNull Long driverId){
        try {
            DriverModel dataModel = driverService.statusToggle(driverId);
            return new ResponseEntity<>(dataModel, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
