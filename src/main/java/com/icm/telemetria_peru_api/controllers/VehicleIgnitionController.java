package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.services.VehicleIgnitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/vehicle-ignition")
@RequiredArgsConstructor
public class VehicleIgnitionController {
    private final VehicleIgnitionService vehicleIgnitionService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleIgnitionModel> findById(@PathVariable Long id) {
        return vehicleIgnitionService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<VehicleIgnitionModel> findAll() {
        return vehicleIgnitionService.findAll();
    }

    @GetMapping("/paged")
    public Page<VehicleIgnitionModel> findAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleIgnitionService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public List<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId) {
        return vehicleIgnitionService.findByVehicleModelId(vehicleId);
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public Page<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleIgnitionService.findByVehicleModelId(vehicleId, pageable);
    }

    @Deprecated
    @GetMapping("/active-durations/{vehicleId}")
    public List<IgnitionDuration> getActiveDurations(@PathVariable Long vehicleId) {
        return vehicleIgnitionService.calculateActiveDurations(vehicleId);
    }

    /**
     * Endpoint to retrieve the ignition counts for a specific vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @return a ResponseEntity containing the ignition counts for the vehicle, or an error message if an exception occurs.
     * */
    @GetMapping("/count/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getCounts(@PathVariable Long vehicleId) {
        try {
            Map<String, Object> data = vehicleIgnitionService.getCounts(vehicleId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(Map.of(), HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve the ignition counts for all months for a specific vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @param year      the year for which to retrieve the ignition count.
     * @return a ResponseEntity containing a list of ignition counts per month, or NOT_FOUND if no data is found.
     */
    @GetMapping("/counts-all-months/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getCountsByMonth(@PathVariable Long vehicleId,
                                                                      @RequestParam(value = "year", required = false) Integer year) {
        try {
            List<Map<String, Object>> counts = vehicleIgnitionService.getIgnitionCountsByMonth(vehicleId, year);

            if (counts.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(counts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to retrieve ignition days count for a specific month and year for a given vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @param year      the year for which to retrieve the ignition count.
     * @param month     the month for which to retrieve the ignition count.
     * @return a ResponseEntity containing the ignition count for that month, or NOT_FOUND if no data is found.
     */
    @GetMapping("/counts-all-days/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getVehicleIgnitionRecords(@PathVariable Long vehicleId,
                                                                               @RequestParam(required = false) Integer year,
                                                                                @RequestParam(required = false) Integer month) {
        try {
            List<Map<String, Object>> records = vehicleIgnitionService.getCountByMonth(vehicleId, year, month);

            if (records.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }

    @PostMapping
    public VehicleIgnitionModel save(@RequestBody VehicleIgnitionModel vehicleIgnitionModel) {
        return vehicleIgnitionService.save(vehicleIgnitionModel);
    }
}
