package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface FuelTheftAlertRepository extends JpaRepository<FuelTheftAlertModel, Long> {

    /**
     * Evita duplicados: ¿ya existe alguna alerta para el vehículo desde "since"?
     * (sin status, porque ya no existe)
     */
    @Query("""
        select (count(a) > 0)
        from FuelTheftAlertModel a
        where a.vehicleModel.id = :vehicleId
          and a.detectedAt >= :since
    """)
    boolean existsSince(
            @Param("vehicleId") Long vehicleId,
            @Param("since") ZonedDateTime since
    );

    /**
     * Variante mejor: evita duplicados por tipo/mensaje desde "since"
     * (útil si quieres permitir 2 mensajes distintos el mismo día)
     */
    @Query("""
        select (count(a) > 0)
        from FuelTheftAlertModel a
        where a.vehicleModel.id = :vehicleId
          and a.message = :message
          and a.detectedAt >= :since
    """)
    boolean existsMessageSince(
            @Param("vehicleId") Long vehicleId,
            @Param("message") String message,
            @Param("since") ZonedDateTime since
    );

    /**
     * Search paginado general (lo que usarás para la tabla).
     * Quité status y dejé solo filtros que existen: vehicleId y rango de fechas.
     */
    @Query("""
        select a
        from FuelTheftAlertModel a
        where (:vehicleId is null or a.vehicleModel.id = :vehicleId)
          and (:start is null or a.detectedAt >= :start)
          and (:end is null or a.detectedAt < :end)
    """)
    Page<FuelTheftAlertModel> search(
            @Param("vehicleId") Long vehicleId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end,
            Pageable pageable
    );
}
