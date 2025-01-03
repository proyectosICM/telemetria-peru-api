package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.services.VehicleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable @NotNull Long vehicleId) {
        VehicleDTO vehicleModel = vehicleService.findById(vehicleId);
        return new ResponseEntity<>(vehicleModel, HttpStatus.OK);
    }

    @GetMapping("/options-data/{vehicleId}")
    public ResponseEntity<VehicleOptionsDTO> findByIdOptions(@PathVariable @NotNull Long vehicleId) {
        VehicleOptionsDTO vehicleModel = vehicleService.findByIdOptions(vehicleId);
        return new ResponseEntity<>(vehicleModel, HttpStatus.OK);
    }

    /** Retrieves vehicles, as a list and paginated. */
    @GetMapping
    public List<VehicleDTO> findAll() {
        return vehicleService.findAll();
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<VehicleDTO>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by status, as a list and paginated. */
    @GetMapping("/findByStatus")
    public List<VehicleDTO> findByStatus(@RequestParam @NotNull Boolean status) {
        return vehicleService.findByStatus(status);
    }
    @GetMapping("/findByStatus-paged")
    public ResponseEntity<Page<VehicleDTO>> findByStatus (@RequestParam @NotNull Boolean status,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType, as a list and paginated. */
    @GetMapping("/findByVehicletype/{vehicletypeId}")
    public List<VehicleDTO> findByVehicleTypeModelId(@PathVariable @NotNull Long vehicletypeId) {
        return vehicleService.findByVehicleTypeModelId(vehicletypeId);
    }
    @GetMapping("/findByVehicletype-paged/{vehicletypeId}")
    public ResponseEntity<Page<VehicleDTO>> findByVehicletypeModelId (@PathVariable @NotNull Long vehicletypeId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByVehicleTypeModelId(vehicletypeId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by company, as a list and paginated. */
    @GetMapping("/findByCompanyId/{companyId}")
    public List<VehicleDTO> findByCompanyModelId(@PathVariable @NotNull Long companyId) {
        return vehicleService.findByCompanyModelId(companyId);
    }
    @GetMapping("/findByCompanyId-Page/{companyId}")
    public ResponseEntity<Page<VehicleDTO>> findByCompanyModelId (@PathVariable @NotNull Long companyId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and company, as a list and paginated. */
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId")
    public List<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelId(@RequestParam @NotNull Long vehicleTypeId, @RequestParam @NotNull Long companyId) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId);
    }
    @GetMapping("/findByVehicleTypeModelIdAndCompanyModelId-page")
    public ResponseEntity<Page<VehicleDTO>> findByVehicleTypeModelIdAndCompanyModelId (
                                                                    @RequestParam @NotNull Long vehicleTypeId,
                                                                    @RequestParam @NotNull Long companyId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByVehicleTypeModelIdAndCompanyModelId(vehicleTypeId, companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves vehicles by vehicleType and compan and status, as a list and paginated. */
    @GetMapping("/findByVehicleTypeIdAndCompanyId")
    public List<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(@RequestParam @NotNull Long vehicleTypeId,
                                                                                 @RequestParam @NotNull Long companyId,
                                                                                 @RequestParam @NotNull Boolean status) {
        return vehicleService.findByVehicleTypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status);
    }
    @GetMapping("/findByVehicleTypeIdAndCompanyId-page")
    public ResponseEntity<Page<VehicleDTO>> findByVehicleTypeModelIdAndCompanyModelIdAndStatus (
            @RequestParam @NotNull Long vehicleTypeId,
            @RequestParam @NotNull Long companyId,
            @RequestParam @NotNull Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByVehicleTypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId,status, pageable);
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

    /** Update vehicle alarm status */
    @PutMapping("/options-update/{vehicleId}")
    public ResponseEntity<VehicleModel> vehicleOptionsUpdate(@PathVariable @NotNull Long vehicleId,
                                                             @RequestParam @NotNull String type,
                                                             @RequestParam @NotNull Boolean status){
        VehicleModel dataModel = vehicleService.vehicleOptionsUpdate(vehicleId, type, status);
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

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> delete(@PathVariable @NotNull Long vehicleId){
        vehicleService.deleteById(vehicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
