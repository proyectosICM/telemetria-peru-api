package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.TireSensorModel;
import com.icm.telemetria_peru_api.services.TireSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/tire-sensor")
@RequiredArgsConstructor
public class TireSensorController {
    private final TireSensorService tireSensorService;

    @GetMapping
    public List<TireSensorModel> findAll(){
        return tireSensorService.findAll();
    }

    @GetMapping("page")
    public Page<TireSensorModel> findAll(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return tireSensorService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TireSensorModel> findById(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @PathVariable Long id) {
        try {
            TireSensorModel data = tireSensorService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-identification-code")
    public ResponseEntity<?> findByIdentificationCode(String code) {
        try {
            Optional<TireSensorModel> data = tireSensorService.findByIdentificationCode(code);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<?> findByCompanyModelId(@PathVariable Long companyId) {
        try {
            List<TireSensorModel> data = tireSensorService.findByCompanyModelId(companyId);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-company-paged/{companyId}")
    public ResponseEntity<?> findByCompanyModelIdPage(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @PathVariable Long companyId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TireSensorModel> data = tireSensorService.findByCompanyModelId(companyId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<?> findByVehicleModelId(@PathVariable Long vehicleId) {
        try {
            List<TireSensorModel> data = tireSensorService.findByCompanyModelId(vehicleId);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<?> findByVehicleModelIdPage(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @PathVariable Long vehicleId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TireSensorModel> data = tireSensorService.findByCompanyModelId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<?> findByStatus(@RequestParam Boolean status) {
        try {
            List<TireSensorModel> data = tireSensorService.findByStatus(status);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-status-paged/{status}")
    public ResponseEntity<?> findByStatusPage(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @PathVariable Boolean status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TireSensorModel> data = tireSensorService.findByStatus(status, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TireSensorModel tireSensorModel){
        try {
            TireSensorModel data = tireSensorService.save(tireSensorModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
