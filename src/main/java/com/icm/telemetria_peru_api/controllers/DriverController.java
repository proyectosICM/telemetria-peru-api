package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.services.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Driver;
import java.util.List;

@RestController
@RequestMapping("api/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<DriverModel> findById(@PathVariable Long driverId) {
        return driverService.findById(driverId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Retrieves drivers, as a list and paginated. */
    @GetMapping
    public List<DriverModel> findAll() {
        return driverService.findAll();
    }
    @GetMapping("/page")
    public ResponseEntity<Page<DriverModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by status, as a list and paginated. */
    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<DriverModel>> findByStatus(@RequestParam Boolean status){
        List<DriverModel> dataModel = driverService.findByStatus(status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
    @GetMapping("/findByStatus-page/{status}")
    public ResponseEntity<Page<DriverModel>> findByStatusPage(@RequestParam Boolean status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company, as a list and paginated. */
    @GetMapping("/findByCompany/{companyId}")
    public ResponseEntity<List<DriverModel>> findByCompanyModelId(@PathVariable Long companyId){
        List<DriverModel> dataModel = driverService.findByCompanyModelId(companyId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompany-page/{companyId}")
    public ResponseEntity<Page<DriverModel>> findByCompanyModelId(@PathVariable Long companyId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company and status, as a list and paginated. */
    @GetMapping("/findByCompanyAndStatus/{companyId}")
    public ResponseEntity<List<DriverModel>> findByCompanyModelIdAndStatus(@PathVariable Long companyId, @RequestParam Boolean status){
        List<DriverModel> dataModel = driverService.findByCompanyModelIdAndStatus(companyId, status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompanyAndStatus-page/{companyId}")
    public ResponseEntity<Page<DriverModel>> findByCompanyModelIdAndStatus(@PathVariable Long companyId,
                                                                  @RequestParam Boolean status,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverModel> dataModel = driverService.findByCompanyModelIdAndStatus(companyId, status , pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<DriverModel> save(@RequestBody @Valid DriverModel driverModel){
        DriverModel dataModel = driverService.save(driverModel);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /**  Main data update */
    @PutMapping("/{driverId}")
    public ResponseEntity<DriverModel> updateMainData(@PathVariable Long driverId, @RequestBody DriverModel driverModel){
        DriverModel dataModel = driverService.updateMainData(driverId, driverModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**  RFID update */
    @PutMapping("/update-RFID/{driverId}")
    public ResponseEntity<DriverModel> updateRFID(@PathVariable Long driverId, @RequestBody String newRFId){
        DriverModel dataModel = driverService.updateRFID(driverId, newRFId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**  status update */
    @PutMapping("/changeStatus/{driverId}")
    public ResponseEntity<DriverModel> changeStatus(@PathVariable Long driverId){
        DriverModel dataModel = driverService.changeStatus(driverId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
