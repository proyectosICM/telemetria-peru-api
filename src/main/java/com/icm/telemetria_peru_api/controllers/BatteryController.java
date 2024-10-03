package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.services.BatteryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/batteries")
public class BatteryController {

    private final BatteryService batteryService;

    public BatteryController(BatteryService batteryService) {
        this.batteryService = batteryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatteryDTO> findById(@PathVariable @NotNull Long id) {
        BatteryDTO gasRecord = batteryService.findById(id);
        return new ResponseEntity<>(gasRecord, HttpStatus.OK);
    }

    @GetMapping
    public List<BatteryDTO> findAll() {
        return batteryService.findAll();
    }

    @GetMapping("/paged")
    public Page<BatteryDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return batteryService.findAll(pageable);
    }



    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<List<BatteryDTO>> findByVehicleId(@PathVariable Long vehicleId) {
        try {
            List<BatteryDTO> data = batteryService.findByVehicleId(vehicleId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByVehicleId-paged/{vehicleId}")
    public ResponseEntity<Page<BatteryDTO>> findByVehicleIdPage(@PathVariable Long vehicleId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BatteryDTO> data = batteryService.findByVehicleId(vehicleId, pageable); // Corrige el tipo de retorno aquí

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByCompanyId/{companyId}")
    public ResponseEntity<List<BatteryDTO>> findByCompanyId(@PathVariable Long companyId) {
        try {
            List<BatteryDTO> data = batteryService.findByCompanyId(companyId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByCompanyId-paged/{companyId}")
    public ResponseEntity<Page<BatteryDTO>> findByCompanyId(@PathVariable Long companyId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BatteryDTO> data = batteryService.findByCompanyId(companyId, pageable);

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<?> save(@RequestBody BatteryModel batteryModel){
        try {
            BatteryModel data = batteryService.save(batteryModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody BatteryModel batteryModel){
        try {
            BatteryModel data = batteryService.update(id, batteryModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            batteryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
