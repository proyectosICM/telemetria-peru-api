package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.services.VehicleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleModel> findById(@PathVariable @NotNull Long vehicleId) {
        return vehicleService.findById(vehicleId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Retrieves vehicles, as a list and paginated. */
    @GetMapping
    public List<VehicleModel> findAll() {
        return vehicleService.findAll();
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<VehicleModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by status, as a list and paginated. */
    @GetMapping("/findByStatus")
    public List<VehicleModel> findByStatus(@RequestParam @NotNull Boolean status) {
        return vehicleService.findByStatus(status);
    }
    @GetMapping("/findByStatus-paged")
    public ResponseEntity<Page<VehicleModel>> findByStatus (@RequestParam @NotNull Boolean status,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType, as a list and paginated. */
    @GetMapping("/findByVehicletype/{vehicletypeId}")
    public List<VehicleModel> findByVehicletypeModelId(@PathVariable @NotNull Long vehicletypeId) {
        return vehicleService.findByVehicletypeModelId(vehicletypeId);
    }
    @GetMapping("/findByVehicletype-paged/{vehicletypeId}")
    public ResponseEntity<Page<VehicleModel>> findByVehicletypeModelId (@PathVariable @NotNull Long vehicletypeId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByVehicletypeModelId(vehicletypeId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by company, as a list and paginated. */
    @GetMapping("/findByCompanyId/{companyId}")
    public List<VehicleModel> findByCompanyModelId(@PathVariable @NotNull Long companyId) {
        return vehicleService.findByCompanyModelId(companyId);
    }
    @GetMapping("/findByCompanyId-Page/{companyId}")
    public ResponseEntity<Page<VehicleModel>> findByCompanyModelId (@PathVariable @NotNull Long companyId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and company, as a list and paginated. */
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId")
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelId(@RequestParam @NotNull Long vehicleTypeId, @RequestParam @NotNull Long companyId) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId);
    }
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId-page")
    public ResponseEntity<Page<VehicleModel>> findByVehicleTypeModelIdAndCompanyModelId (
                                                                    @RequestParam @NotNull Long vehicleTypeId,
                                                                    @RequestParam @NotNull Long companyId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleModel> dataModel = vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and compan and status, as a list and paginated. */
    @GetMapping("/findByVehicleTypeIdAndCompanyId")
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(@RequestParam @NotNull Long vehicleTypeId,
                                                                                 @RequestParam @NotNull Long companyId,
                                                                                 @RequestParam @NotNull Boolean status) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status);
    }
    @GetMapping("/findByVehicleTypeIdAndCompanyId-page")
    public ResponseEntity<Page<VehicleModel>> findByVehicleTypeModelIdAndCompanyModelIdAndStatus (
            @RequestParam @NotNull Long vehicleTypeId,
            @RequestParam @NotNull Long companyId,
            @RequestParam @NotNull Boolean status,
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
    public ResponseEntity<VehicleModel> update(@PathVariable @NotNull Long vehicleId, @RequestBody @Valid VehicleModel vehicleModel){
        VehicleModel dataModel = vehicleService.updateMainData(vehicleId, vehicleModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Change vehicle driver */
    @PutMapping("/change-driver/{vehicleId}")
    public ResponseEntity<VehicleModel> changeDriver(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Long driverId){
        VehicleModel dataModel = vehicleService.changeDriver(vehicleId, driverId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Update vehicle location */
    @PutMapping("/update-location/{vehicleId}")
    public ResponseEntity<VehicleModel> changeLocation(@PathVariable @NotNull Long vehicleId,
                                                       @RequestParam @NotNull BigDecimal longitud,
                                                       @RequestParam @NotNull BigDecimal latitud){
        VehicleModel dataModel = vehicleService.changeLocation(vehicleId, longitud, latitud);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle alarm status */
    @PutMapping("/update-alarm/{vehicleId}")
    public ResponseEntity<VehicleModel> alarmStatusUpdate(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Boolean alarm){
        VehicleModel dataModel = vehicleService.alarmStatusUpdate(vehicleId, alarm);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle engine status */
    @PutMapping("/update-engine/{vehicleId}")
    public ResponseEntity<VehicleModel> engineStatusUpdate(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Boolean engine) {
        VehicleModel dataModel = vehicleService.engineStatusUpdate(vehicleId, engine);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle lock status */
    @PutMapping("/update-lock/{vehicleId}")
    public ResponseEntity<VehicleModel> speedUpdate(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Boolean lock){
        VehicleModel dataModel = vehicleService.lockUpdate(vehicleId, lock);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle speed */
    @PutMapping("/update-speed/{vehicleId}")
    public ResponseEntity<VehicleModel> speedUpdate(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Integer speed){
        VehicleModel dataModel = vehicleService.speedUpdate(vehicleId, speed);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update vehicle time on */
    @PutMapping("/update-time-on/{vehicleId}")
    public ResponseEntity<VehicleModel> timeOnUpdate(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Long timeOn){
        VehicleModel dataModel = vehicleService.timeOnUpdate(vehicleId, timeOn);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Toggle vehicle status */
    @PatchMapping("/change-status/{vehicleId}")
    public ResponseEntity<VehicleModel> changeStatus(@PathVariable @NotNull Long vehicleId){
        VehicleModel dataModel = vehicleService.changeStatus(vehicleId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
}
