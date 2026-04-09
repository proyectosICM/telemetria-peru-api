package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.dto.DvrGpsSnapshotDTO;
import com.icm.telemetria_peru_api.enums.GpsSource;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.repositories.VehicleSnapshotRepository;
import com.icm.telemetria_peru_api.services.VehicleSnapshotService;
import com.icm.telemetria_peru_api.utils.DvrPhoneNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleSnapshotServiceImpl implements VehicleSnapshotService {
    private final VehicleSnapshotRepository vehicleSnapshotRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleSnapshotModel getSnapshotById(Long id) {
        return vehicleSnapshotRepository.findById(id)
                .map(this::toEffectiveSnapshot)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found with id: " + id));
    }

    @Override
    public VehicleSnapshotModel getLatestSnapshotByVehicleId(Long vehicleId) {
        return vehicleSnapshotRepository.findAll()
                .stream()
                .filter(snapshot -> snapshot.getVehicleModel().getId().equals(vehicleId))
                .max((s1, s2) -> s1.getId().compareTo(s2.getId()))
                .map(this::toEffectiveSnapshot)
                .orElseThrow(() -> new IllegalArgumentException("No snapshots found for vehicle with id: " + vehicleId));
    }

    @Override
    public List<VehicleSnapshotModel> getAllSnapshots() {
        return vehicleSnapshotRepository.findAll().stream()
                .map(this::toEffectiveSnapshot)
                .toList();
    }

    @Override
    public List<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId) {
        return vehicleSnapshotRepository.findByVehicleModelId(vehicleId).stream()
                .map(this::toEffectiveSnapshot)
                .toList();
    }

    @Override
    public Page<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId, Pageable pageable) {
        return vehicleSnapshotRepository.findByVehicleModelId(vehicleId, pageable)
                .map(this::toEffectiveSnapshot);
    }

    @Override
    public List<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId) {
        return vehicleSnapshotRepository.findByCompanyModelId(companyId).stream()
                .map(this::toEffectiveSnapshot)
                .toList();
    }

    @Override
    public Page<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId, Pageable pageable) {
        return vehicleSnapshotRepository.findByCompanyModelId(companyId, pageable)
                .map(this::toEffectiveSnapshot);
    }

    @Override
    public void saveSnapshot(VehicleSnapshotModel snapshot) {
        vehicleSnapshotRepository.save(snapshot);
    }

    @Override
    public VehicleSnapshotModel updateOrCreateSnapshotByVehicleId(Long vehicleId, VehicleSnapshotModel newSnapshotData) {
        VehicleSnapshotModel existingSnapshot = vehicleSnapshotRepository
                .findTopByVehicleModelIdOrderByIdDesc(vehicleId)
                .orElse(null);

        if (existingSnapshot != null) {
            existingSnapshot.setSnapshotIgnitionStatus(newSnapshotData.getSnapshotIgnitionStatus());
            existingSnapshot.setSnapshotAlarmStatus(newSnapshotData.getSnapshotAlarmStatus());
            existingSnapshot.setSnapshotSpeed(newSnapshotData.getSnapshotSpeed());
            existingSnapshot.setSnapshotLatitude(newSnapshotData.getSnapshotLatitude());
            existingSnapshot.setSnapshotLongitude(newSnapshotData.getSnapshotLongitude());
            existingSnapshot.setSnapshotFuelLevel(newSnapshotData.getSnapshotFuelLevel());
            return vehicleSnapshotRepository.save(existingSnapshot);
        }

        return vehicleSnapshotRepository.save(newSnapshotData);
    }

    @Override
    public VehicleSnapshotModel updateOrCreateDvrSnapshotByPhone(String dvrPhone, DvrGpsSnapshotDTO dvrSnapshotData) {
        String normalizedPhone = DvrPhoneNormalizer.normalize(dvrPhone);
        if (normalizedPhone == null) {
            throw new IllegalArgumentException("dvrPhone invalido");
        }

        VehicleModel vehicle = vehicleRepository.findByDvrPhone(normalizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("No existe vehiculo para dvrPhone: " + normalizedPhone));

        VehicleSnapshotModel snapshot = vehicleSnapshotRepository
                .findTopByVehicleModelIdOrderByIdDesc(vehicle.getId())
                .orElseGet(() -> {
                    VehicleSnapshotModel created = new VehicleSnapshotModel();
                    created.setVehicleModel(vehicle);
                    created.setCompanyModel(vehicle.getCompanyModel());
                    return created;
                });

        snapshot.setDvrSnapshotLatitude(dvrSnapshotData.getLatitude());
        snapshot.setDvrSnapshotLongitude(dvrSnapshotData.getLongitude());
        snapshot.setDvrSnapshotSpeed(dvrSnapshotData.getSpeed());
        snapshot.setDvrSnapshotIgnitionStatus(dvrSnapshotData.getIgnitionStatus());
        snapshot.setDvrSnapshotAlarmStatus(dvrSnapshotData.getAlarmStatus());
        snapshot.setDvrSnapshotTimestamp(dvrSnapshotData.getTimestamp());

        return vehicleSnapshotRepository.save(snapshot);
    }

    private VehicleSnapshotModel toEffectiveSnapshot(VehicleSnapshotModel snapshot) {
        VehicleSnapshotModel effective = new VehicleSnapshotModel();
        effective.setId(snapshot.getId());
        effective.setVehicleModel(snapshot.getVehicleModel());
        effective.setCompanyModel(snapshot.getCompanyModel());
        effective.setCreatedAt(snapshot.getCreatedAt());
        effective.setUpdatedAt(snapshot.getUpdatedAt());
        effective.setSnapshotFuelLevel(snapshot.getSnapshotFuelLevel());
        effective.setDvrSnapshotLatitude(snapshot.getDvrSnapshotLatitude());
        effective.setDvrSnapshotLongitude(snapshot.getDvrSnapshotLongitude());
        effective.setDvrSnapshotSpeed(snapshot.getDvrSnapshotSpeed());
        effective.setDvrSnapshotIgnitionStatus(snapshot.getDvrSnapshotIgnitionStatus());
        effective.setDvrSnapshotAlarmStatus(snapshot.getDvrSnapshotAlarmStatus());
        effective.setDvrSnapshotTimestamp(snapshot.getDvrSnapshotTimestamp());

        if (resolveEffectiveGpsSource(snapshot.getVehicleModel()) == GpsSource.DVR) {
            effective.setSnapshotLatitude(snapshot.getDvrSnapshotLatitude());
            effective.setSnapshotLongitude(snapshot.getDvrSnapshotLongitude());
            effective.setSnapshotSpeed(snapshot.getDvrSnapshotSpeed());
            effective.setSnapshotIgnitionStatus(snapshot.getDvrSnapshotIgnitionStatus());
            effective.setSnapshotAlarmStatus(snapshot.getDvrSnapshotAlarmStatus());
        } else {
            effective.setSnapshotLatitude(snapshot.getSnapshotLatitude());
            effective.setSnapshotLongitude(snapshot.getSnapshotLongitude());
            effective.setSnapshotSpeed(snapshot.getSnapshotSpeed());
            effective.setSnapshotIgnitionStatus(snapshot.getSnapshotIgnitionStatus());
            effective.setSnapshotAlarmStatus(snapshot.getSnapshotAlarmStatus());
        }

        return effective;
    }

    private GpsSource resolveEffectiveGpsSource(VehicleModel vehicle) {
        GpsSource configured = vehicle.getGpsSource() != null ? vehicle.getGpsSource() : GpsSource.AUTO;
        if (configured != GpsSource.AUTO) {
            return configured;
        }

        boolean hasImei = vehicle.getImei() != null && !vehicle.getImei().isBlank();
        boolean hasDvr = vehicle.getDvrPhone() != null && !vehicle.getDvrPhone().isBlank();

        if (hasDvr && !hasImei) {
            return GpsSource.DVR;
        }
        if (hasImei && !hasDvr) {
            return GpsSource.IMEI;
        }
        return GpsSource.IMEI;
    }
}
