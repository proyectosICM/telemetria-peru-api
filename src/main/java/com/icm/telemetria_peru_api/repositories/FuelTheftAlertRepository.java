package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface FuelTheftAlertRepository extends JpaRepository<FuelTheftAlertModel, Long> {

    @Query("""
        select (count(a) > 0)
        from FuelTheftAlertModel a
        where a.vehicleModel.id = :vehicleId
          and a.status = 'OPEN'
          and a.detectedAt >= :since
    """)
    boolean existsOpenSince(@Param("vehicleId") Long vehicleId, @Param("since") ZonedDateTime since);
}
