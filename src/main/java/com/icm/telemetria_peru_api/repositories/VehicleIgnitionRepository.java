package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.dto.IgnitionCountByDate;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleIgnitionRepository extends JpaRepository<VehicleIgnitionModel, Long> {
    List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    VehicleIgnitionModel findTopByVehicleModelOrderByCreatedAtDesc(VehicleModel  vehicleModel);
    List<VehicleIgnitionModel> findByVehicleModelIdOrderByCreatedAt(Long vehicleId);

    @Query("SELECT new com.icm.telemetria_peru_api.dto.IgnitionCountByDate(" +
            "CAST(FUNCTION('DATE', vi.createdAt) AS LocalDate), COUNT(vi)) " +
            "FROM VehicleIgnitionModel vi " +
            "WHERE vi.vehicleModel.id = :vehicleId " +
            "AND vi.createdAt >= CURRENT_DATE - 7 " +
            "GROUP BY FUNCTION('DATE', vi.createdAt) " +
            "ORDER BY FUNCTION('DATE', vi.createdAt) DESC")
    List<IgnitionCountByDate> countIgnitionsByWeek(Long vehicleId);
}
