package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.projections.FuelEfficiencySumView;

import java.time.LocalDate;
import java.util.List;

public interface FuelEfficiencyService {
    // ===== Crear / Upsert (1 registro por vehicle + day) =====
    FuelEfficiencyModel upsertDaily(Long vehicleId, LocalDate day,
                                    Long parkedSeconds, Long idleSeconds, Long operationSeconds);

    // Sumar delta a lo ya existente (muy útil para ir acumulando)
    FuelEfficiencyModel addSeconds(Long vehicleId, LocalDate day,
                                   Long parkedSecondsDelta, Long idleSecondsDelta, Long operationSecondsDelta);

    // ===== Get por ID =====
    FuelEfficiencyModel getById(Long id);

    // ===== Get por vehicle + day =====
    FuelEfficiencyModel getByVehicleAndDay(Long vehicleId, LocalDate day);

    // ===== Listar día =====
    List<FuelEfficiencyModel> listByDay(LocalDate day);
    List<FuelEfficiencyModel> listByVehicleAndDay(Long vehicleId, LocalDate day);
    List<FuelEfficiencyModel> listByCompanyAndDay(Long companyId, LocalDate day);

    // ===== Listar rango (para semana/mes/año) =====
    List<FuelEfficiencyModel> listByVehicleAndRange(Long vehicleId, LocalDate start, LocalDate end);
    List<FuelEfficiencyModel> listByCompanyAndRange(Long companyId, LocalDate start, LocalDate end);

    // ===== SUM rango =====
    FuelEfficiencySumView sumByVehicleAndRange(Long vehicleId, LocalDate start, LocalDate end);
    FuelEfficiencySumView sumByCompanyAndRange(Long companyId, LocalDate start, LocalDate end);

    // ===== Delete (por si necesitas admin) =====
    void deleteById(Long id);
}
