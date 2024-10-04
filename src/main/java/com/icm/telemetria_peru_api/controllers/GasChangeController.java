package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.GasChangeDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.services.GasChangeService;
import jakarta.validation.Valid;
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
@RequestMapping("api/gas-changes")
public class GasChangeController {
    private final GasChangeService gasChangeService;

    @Autowired
    public GasChangeController(GasChangeService gasChangeService) {
        this.gasChangeService = gasChangeService;
    }

    @GetMapping("/{gasChangeId}")
    public ResponseEntity<GasChangeModel> findById(@PathVariable @NotNull Long gasChangeId) {
        return gasChangeService.findById(gasChangeId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<GasChangeModel> findAll() {
        return gasChangeService.findAll();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<GasChangeModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GasChangeModel> dataModel = gasChangeService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByVehicleModelId")
    public ResponseEntity<List<GasChangeModel>> findByVehicleModelId(@RequestParam @NotNull Long vehicleId){
        List<GasChangeModel> dataModel = gasChangeService.findByVehicleModelId(vehicleId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
    @GetMapping("/findByVehicleModelId-paged")
    public ResponseEntity<Page<GasChangeModel>> findByVehicleModelId(@RequestParam @NotNull Long vehicleId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<GasChangeModel> dataModel = gasChangeService.findByVehicleModelId(vehicleId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid GasChangeDTO gasChangeDTO){
        try {
            GasChangeModel dataModel = gasChangeService.saveFromDTO(gasChangeDTO);
            return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{gasChangeId}")
    public ResponseEntity<Object> delete(@PathVariable @NotNull Long gasChangeId){
        gasChangeService.deleteById(gasChangeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
