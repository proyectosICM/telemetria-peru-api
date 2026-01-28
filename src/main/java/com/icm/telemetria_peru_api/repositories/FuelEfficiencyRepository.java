package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.projections.FuelEfficiencySumView;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FuelEfficiencyRepository extends JpaRepository<FuelEfficiencyModel, Long> {
    // ========= Base (útil para upsert) =========
    Optional<FuelEfficiencyModel> findByVehicleModel_IdAndDay(Long vehicleId, LocalDate day);

    // ========= Listar día =========
    List<FuelEfficiencyModel> findAllByDay(LocalDate day);

    List<FuelEfficiencyModel> findAllByVehicleModel_IdAndDay(Long vehicleId, LocalDate day);

    // Si tu VehicleModel tiene companyId directo:
    List<FuelEfficiencyModel> findAllByVehicleModel_CompanyIdAndDay(Long companyId, LocalDate day);

    // Si en cambio es vehicleModel.companyModel.id, usa esto:
    // List<FuelEfficiencyModel> findAllByVehicleModel_CompanyModel_IdAndDay(Long companyId, LocalDate day);

    // ========= Listar por rango (semana/mes/año -> tú le pasas start/end) =========
    List<FuelEfficiencyModel> findAllByVehicleModel_IdAndDayBetween(Long vehicleId, LocalDate start, LocalDate end);

    List<FuelEfficiencyModel> findAllByVehicleModel_CompanyIdAndDayBetween(Long companyId, LocalDate start, LocalDate end);

    // ========= SUM por rango (un solo vehículo) =========
    @Query("""
        select
            coalesce(sum(f.parkedSeconds), 0) as parkedSeconds,
            coalesce(sum(f.idleSeconds), 0) as idleSeconds,
            coalesce(sum(f.operationSeconds), 0) as operationSeconds
        from FuelEfficiencyModel f
        where f.vehicleModel.id = :vehicleId
          and f.day between :start and :end
    """)
    FuelEfficiencySumView sumByVehicleAndRange(
            @Param("vehicleId") Long vehicleId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // ========= SUM por rango (toda una empresa) =========
    @Query("""
        select
            coalesce(sum(f.parkedSeconds), 0) as parkedSeconds,
            coalesce(sum(f.idleSeconds), 0) as idleSeconds,
            coalesce(sum(f.operationSeconds), 0) as operationSeconds
        from FuelEfficiencyModel f
        where f.vehicleModel.companyId = :companyId
          and f.day between :start and :end
    """)
    FuelEfficiencySumView sumByCompanyAndRange(
            @Param("companyId") Long companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // ========= (Opcional) SUM agrupado por día para gráficas =========
    @Query("""
        select f
        from FuelEfficiencyModel f
        where f.vehicleModel.id = :vehicleId
          and f.day between :start and :end
        order by f.day asc
    """)
    List<FuelEfficiencyModel> listDailyByVehicle(
            @Param("vehicleId") Long vehicleId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
