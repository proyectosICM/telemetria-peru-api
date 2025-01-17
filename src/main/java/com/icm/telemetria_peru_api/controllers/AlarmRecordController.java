package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.AlarmRecordDTO;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.services.AlarmRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/alarm-record")
@RequiredArgsConstructor
public class AlarmRecordController {
    private final AlarmRecordService alarmRecordService;

    @GetMapping
    public List<AlarmRecordDTO> findAll(){
        return alarmRecordService.findAll();
    }

    @GetMapping("/paged")
    public Page<AlarmRecordDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return alarmRecordService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<AlarmRecordDTO>> findByVehicleModelId(@PathVariable Long vehicleId){
        try {
            List<AlarmRecordDTO> data =  alarmRecordService.findByVehicleModelId(vehicleId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<AlarmRecordDTO>> findByVehicleModelId(@PathVariable Long vehicleId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<AlarmRecordDTO> data = alarmRecordService.findByVehicleModelId(vehicleId, pageable);

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Page.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<AlarmRecordModel> save(@RequestBody AlarmRecordModel alarmRecordModel){
        try {
            AlarmRecordModel data =  alarmRecordService.save(alarmRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlarmRecordModel> delete(@PathVariable Long id){
        alarmRecordService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
