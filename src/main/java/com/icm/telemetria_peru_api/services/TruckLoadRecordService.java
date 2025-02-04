package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
import com.icm.telemetria_peru_api.repositories.TruckLoadRecordRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TruckLoadRecordService {
    private final TruckLoadRecordRepository truckLoadRecordRepository;
    private final DateUtils dateUtils;

    /** */
    public long countRecordsByVehicleAndDate(Long vehicleId, LocalDate date) {
        return truckLoadRecordRepository.countByVehicleModelIdAndDate(vehicleId, date);
    }

    public List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month) {
        List<Map<String, Object>> timestamps = dateUtils.getMonthTimestamps(year, month);
        long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
        long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

        ZonedDateTime startTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(startTimestampSeconds), ZoneId.of("America/Lima"));
        ZonedDateTime endTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(endTimestampSeconds), ZoneId.of("America/Lima"));

        List<TruckLoadRecordModel> records = truckLoadRecordRepository.findByVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestamp, endTimestamp);

        Map<LocalDate, Long> groupedByDay = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt()
                                .withZoneSameInstant(ZoneId.of("America/Lima"))
                                .toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : groupedByDay.entrySet()) {
            Map<String, Object> result = new HashMap<>();
            result.put("day", entry.getKey().atStartOfDay(ZoneId.of("America/Lima")).toEpochSecond());
            result.put("recordCount", entry.getValue());
            results.add(result);
        }

        return results;
    }

    /** */
    public Page<Map<String, Object>> getDailyLoadCountsByVehicle(Long vehicleId, Pageable pageable) {
        return truckLoadRecordRepository.findDailyRecordCountsByVehicle(vehicleId, pageable);
    }

    public Optional<TruckLoadRecordModel> findById(Long id) {
        return truckLoadRecordRepository.findById(id);
    }

    public List<TruckLoadRecordModel> findAll(){
        return truckLoadRecordRepository.findAll();
    }

    public Page<TruckLoadRecordModel> findAll(Pageable pageable){
        return truckLoadRecordRepository.findAll(pageable);
    }

    public List<TruckLoadRecordModel> findByVehicleId(Long vehicleId){
        return truckLoadRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<TruckLoadRecordModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return truckLoadRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public TruckLoadRecordModel save(TruckLoadRecordModel truckLoadRecordModel){
        return truckLoadRecordRepository.save(truckLoadRecordModel);
    }
}
