package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.dto.GasChangeDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.services.GasChangeService;
import com.icm.telemetria_peru_api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GasChangeServiceImpl implements GasChangeService {
    private final GasChangeRepository gasChangeRepository;
    private final VehicleRepository vehicleRepository;
    private final DateUtils dateUtils;

    @Override
    public Optional<GasChangeModel> findById(Long gasChangeId) {
        return gasChangeRepository.findById(gasChangeId);
    }

    @Override
    public List<GasChangeModel> findByVehicleModelId(Long vehicleId) {
        return gasChangeRepository.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<GasChangeModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return gasChangeRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /* public List<Map<String, Object>> counts(Long vehicleId,Integer day, Integer month,  Integer year) {
         List<Map<String, Object>> timestamps = dateUtils.getDayTimeStamps(year, month);
         long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
         long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

          List records gasChangeRepository.findByVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestampSeconds, endTimestampSeconds);

     }
 */
    @Override
    public GasChangeModel saveFromDTO(GasChangeDTO gasChangeDTO) {
        GasChangeModel gasChangeModel = gasChangeDTO.toGasChangeModel();

        Optional<VehicleModel> vehicleModelOptional = vehicleRepository.findById(gasChangeDTO.getVehicleModelId());
        if (vehicleModelOptional.isEmpty()) {
            throw new IllegalArgumentException("VehicleModel with ID " + gasChangeDTO.getVehicleModelId() + " not found");
        }

        gasChangeModel.setVehicleModel(vehicleModelOptional.get());

        return gasChangeRepository.save(gasChangeModel);
    }

    @Override
    public GasChangeModel save(GasChangeModel gasChangeModel){
        return gasChangeRepository.save(gasChangeModel);
    }

    @Override
    public void deleteById(Long id){
        gasChangeRepository.deleteById(id);
    }
}
