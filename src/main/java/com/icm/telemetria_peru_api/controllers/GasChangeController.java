package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.GasChangeDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.services.GasChangeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/gas-changes")
@RequiredArgsConstructor
public class GasChangeController {
    private final GasChangeService gasChangeService;

    @GetMapping("/{gasChangeId}")
    public ResponseEntity<GasChangeModel> findById(@PathVariable @NotNull Long gasChangeId) {
        return gasChangeService.findById(gasChangeId)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<GasChangeModel>> findByVehicleModelId(@PathVariable @NotNull Long vehicleId) {
        try {
            List<GasChangeModel> dataModel = gasChangeService.findByVehicleModelId(vehicleId);

            if (dataModel.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(dataModel, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<GasChangeModel>> findByVehicleModelId(@PathVariable @NotNull Long vehicleId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<GasChangeModel> dataModel = gasChangeService.findByVehicleModelId(vehicleId, pageable);

            if (dataModel.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(dataModel);

        } catch (DataAccessException e) { // Error en la BD
            System.out.println("Error de base de datos al obtener registros de cambio de gas para vehicleId: " + vehicleId);
            e.printStackTrace(); // Para mostrar el error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) { // Otros errores inesperados
            System.out.println("Error inesperado al obtener registros de cambio de gas para vehicleId: " + vehicleId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid GasChangeDTO gasChangeDTO) {
        try {
            GasChangeModel dataModel = gasChangeService.saveFromDTO(gasChangeDTO);
            return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody @Valid GasChangeModel gasChangeModel) {
        try {
            GasChangeModel dataModel = gasChangeService.save(gasChangeModel);
            return new ResponseEntity<>(dataModel, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{gasChangeId}")
    public ResponseEntity<Object> delete(@PathVariable @NotNull Long gasChangeId) {
        gasChangeService.deleteById(gasChangeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
