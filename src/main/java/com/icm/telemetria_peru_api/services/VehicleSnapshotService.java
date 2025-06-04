package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import com.icm.telemetria_peru_api.repositories.VehicleSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleSnapshotService {

    private final VehicleSnapshotRepository vehicleSnapshotRepository;

    public VehicleSnapshotModel getSnapshotById(Long id) {
        return vehicleSnapshotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found with id: " + id));
    }

    public VehicleSnapshotModel getLatestSnapshotByVehicleId(Long vehicleId) {
        return vehicleSnapshotRepository.findAll()
                .stream()
                .filter(snapshot -> snapshot.getVehicleModel().getId().equals(vehicleId))
                .max((s1, s2) -> s1.getId().compareTo(s2.getId())) // Assuming ID is sequential
                .orElseThrow(() -> new IllegalArgumentException("No snapshots found for vehicle with id: " + vehicleId));
    }

    public List<VehicleSnapshotModel> getAllSnapshots() {
        return vehicleSnapshotRepository.findAll();
    }

    public List<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId) {
        return vehicleSnapshotRepository.findByVehicleModelId(vehicleId);
    }

    public Page<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId, Pageable pageable) {
        return vehicleSnapshotRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public List<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId) {
        return vehicleSnapshotRepository.findByCompanyModelId(companyId);
    }

    public Page<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId, Pageable pageable) {
        return vehicleSnapshotRepository.findByCompanyModelId(companyId, pageable);
    }

    public void saveSnapshot(VehicleSnapshotModel snapshot) {
        vehicleSnapshotRepository.save(snapshot);
    }

    public VehicleSnapshotModel updateOrCreateSnapshotByVehicleId(Long vehicleId, VehicleSnapshotModel newSnapshotData) {
        // Buscar el snapshot más reciente del vehículo
        VehicleSnapshotModel existingSnapshot = vehicleSnapshotRepository
                .findTopByVehicleModelIdOrderByIdDesc(vehicleId) // necesitas este método en tu repositorio
                .orElse(null);

        if (existingSnapshot != null) {
            // Si existe, actualizar campos (ajusta según tus campos reales)
            existingSnapshot.setSnapshotIgnitionStatus(newSnapshotData.getSnapshotIgnitionStatus());
            existingSnapshot.setSnapshotAlarmStatus(newSnapshotData.getSnapshotAlarmStatus());
            existingSnapshot.setSnapshotSpeed(newSnapshotData.getSnapshotSpeed());
            existingSnapshot.setSnapshotLatitude(newSnapshotData.getSnapshotLatitude());
            existingSnapshot.setSnapshotLongitude(newSnapshotData.getSnapshotLongitude());
            existingSnapshot.setSnapshotFuelLevel(newSnapshotData.getSnapshotFuelLevel());
            //existingSnapshot.setTimestamp(newSnapshotData.getTimestamp());
            // ...otros campos que quieras actualizar

            return vehicleSnapshotRepository.save(existingSnapshot);
        } else {
            // Si no existe, crear nuevo snapshot
            return vehicleSnapshotRepository.save(newSnapshotData);
        }
    }
}
