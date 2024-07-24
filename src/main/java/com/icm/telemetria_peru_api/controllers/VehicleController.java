package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.services.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleModel> findById(@PathVariable Long vehicleId) {
        return vehicleService.findById(vehicleId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Retrieves vehicles, as a list and paginated. */
    @GetMapping
    public List<VehicleModel> findAll() {
        return vehicleService.findAll();
    }
    @GetMapping("/page")
    public ResponseEntity<Page<VehicleModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by status, as a list and paginated. */
    @GetMapping("/findByStatus/{status}")
    public List<VehicleModel> findByStatus(@RequestParam Boolean status) {
        return vehicleService.findByStatus(status);
    }
    @GetMapping("/findByStatus-page/{status}")
    public ResponseEntity<Page<VehicleModel>> findByStatus (@RequestParam Boolean status,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType, as a list and paginated. */
    @GetMapping("/findByVehicletype/{vehicletypeId}")
    public List<VehicleModel> findByVehicletypeModelId(@PathVariable Long vehicletypeId) {
        return vehicleService.findByVehicletypeModelId(vehicletypeId);
    }
    @GetMapping("/findByVehicletype-page/{vehicletypeId}")
    public ResponseEntity<Page<VehicleModel>> findByVehicletypeModelId (@PathVariable Long vehicletypeId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByVehicletypeModelId(vehicletypeId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by company, as a list and paginated. */
    @GetMapping("/findByCompanyId/{companyId}")
    public List<VehicleModel> findByCompanyModelId(@PathVariable Long companyId) {
        return vehicleService.findByCompanyModelId(companyId);
    }
    @GetMapping("/findByCompanyId-Page/{companyId}")
    public ResponseEntity<Page<VehicleModel>> findByCompanyModelId (@PathVariable Long companyId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and company, as a list and paginated. */
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId")
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelId(@RequestParam Long vehicleTypeId, @RequestParam Long companyId) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId);
    }
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId-page")
    public ResponseEntity<Page<VehicleModel>> findByVehicleTypeModelIdAndCompanyModelId (
                                                                    @RequestParam Long vehicleTypeId,
                                                                    @RequestParam Long companyId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and compan and status, as a list and paginated. */
    @GetMapping("/findByVehicleTypeIdAndCompanyId")
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(@RequestParam Long vehicleTypeId,
                                                                                 @RequestParam Long companyId,
                                                                                 @RequestParam Boolean status) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status);
    }
    @GetMapping("/findByVehicleTypeIdAndCompanyId-page")
    public ResponseEntity<Page<VehicleModel>> findByVehicleTypeModelIdAndCompanyModelIdAndStatus (
            @RequestParam Long vehicleTypeId,
            @RequestParam Long companyId,
            @RequestParam Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByVehicleTypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId,status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<VehicleModel> save(@RequestBody @Valid VehicleModel vehicleModel){
        VehicleModel dataModel = vehicleService.save(vehicleModel);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /**  Main data update */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleModel> update(@PathVariable Long vehicleId, @RequestBody VehicleModel vehicleModel){
        VehicleModel dataModel = vehicleService.updateMainData(vehicleId, vehicleModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Change vehicle driver */
    @PutMapping("/change-driver")
    public ResponseEntity<VehicleModel> changeDriver(@RequestParam Long vehicleId, @RequestParam Long driverId){
        VehicleModel dataModel = vehicleService.changeDriver(vehicleId, driverId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Update vehicle location */
    @PutMapping("/update-location")
    public ResponseEntity<VehicleModel> changeLocation(@PathVariable Long vehicleId,
                                                       @RequestParam @Valid BigDecimal longitud,
                                                       @RequestParam @Valid BigDecimal latitud){
        VehicleModel dataModel = vehicleService.changeLocation(vehicleId, longitud, latitud);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle alarm status */
    @PutMapping("/update-alarm")
    public ResponseEntity<VehicleModel> alarmStatusUpdate(@PathVariable Long vehicleId, @RequestParam @Valid Boolean alarm){
        VehicleModel dataModel = vehicleService.alarmStatusUpdate(vehicleId, alarm);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle speed */
    @PutMapping("/update-speed")
    public ResponseEntity<VehicleModel> speedUpdate(@PathVariable Long vehicleId, @RequestParam @Valid Integer speed){
        VehicleModel dataModel = vehicleService.speedUpdate(vehicleId, speed);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle time on */
    @PutMapping("/update-time-on")
    public ResponseEntity<VehicleModel> timeOnUpdate(@PathVariable Long vehicleId, @RequestParam @Valid Long timeOn){
        VehicleModel dataModel = vehicleService.timeOnUpdate(vehicleId, timeOn);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Toggle vehicle status */
    @PatchMapping("/change-status")
    public ResponseEntity<VehicleModel> changeStatus(@PathVariable Long vehicleId){
        VehicleModel dataModel = vehicleService.changeStatus(vehicleId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
}
