package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import com.icm.telemetria_peru_api.integration.mqtt.handlers.IgnitionHandler;

@Repository
public interface VehicleIgnitionRepository extends JpaRepository<VehicleIgnitionModel, Long> {
    List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId);

    Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    /**
     * Retrieves the most recent ignition log associated with a given vehicle.
     * Used by {@link IgnitionHandler} to toggle the ignition log based on real-time information.
     *
     * @param vehicleModel the {@link VehicleModel} for which the most recent ignition log is retrieved.
     * @return the most recent {@link VehicleIgnitionModel} associated with the given vehicle model,
     *         or {@code null} if no ignition record exists.
     */
    VehicleIgnitionModel findTopByVehicleModelOrderByCreatedAtDesc(VehicleModel vehicleModel);

    List<VehicleIgnitionModel> findByVehicleModelIdOrderByCreatedAt(Long vehicleId);

    /**
     * Counts the number of ignition events for a vehicle on the current day.
     *
     * @param vehicleId the ID of the vehicle for which to count ignition events.
     * @return a list of maps where each map contains:
     *         - "day" (String): the formatted date (YYYY-MM-DD).
     *         - "count" (Long): the number of ignition events on that day.
     */
    @Query(value = """ 
                SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
                       COUNT(vi.status) AS count 
                FROM vehicle_ignition vi 
                WHERE vi.vehicle_id = :vehicleId
                  AND vi.status = true
                  AND DATE(vi.created_at) = CURRENT_DATE
                GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
            """, nativeQuery = true)
    List<Map<String, Object>> countsDay(@Param("vehicleId") Long vehicleId);

    /**
     * Counts the number of ignition events for a vehicle during the current week.
     *
     * @param vehicleId the ID of the vehicle for which to count ignition events.
     * @return a list of maps where each map contains:
     *         - "day" (String): the formatted date (YYYY-MM-DD) within the current week.
     *         - "count" (Long): the number of ignition events on that day.
     *         Results are ordered by date in descending order.
     */
    @Query(value = """ 
                SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
                       COUNT(vi.status) AS count 
                FROM vehicle_ignition vi 
                WHERE vi.vehicle_id = :vehicleId
                  AND vi.status = true
                  AND YEAR(vi.created_at) = YEAR(CURRENT_DATE) 
                  AND WEEK(vi.created_at, 1) = WEEK(CURRENT_DATE, 1) 
                GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
                ORDER BY day DESC  
            """, nativeQuery = true)
    List<Map<String, Object>> countsWeek(@Param("vehicleId") Long vehicleId);

    /**
     * Counts the number of ignition events for a vehicle during the current month.
     *
     * @param vehicleId the ID of the vehicle for which to count ignition events.
     * @return a list of maps where each map contains:
     *         - "day" (String): the formatted date (YYYY-MM-DD) within the current month.
     *         - "count" (Long): the number of ignition events on that day.
     *         Results are ordered by date in descending order.
     */
    @Query(value = """ 
                SELECT DATE_FORMAT(vi.created_at, '%Y-%m-%d') AS day, 
                       COUNT(vi.status) AS count 
                FROM vehicle_ignition vi 
                WHERE vi.vehicle_id = :vehicleId
                  AND vi.status = true
                  AND MONTH(vi.created_at) = MONTH(CURRENT_DATE)  
                  AND YEAR(vi.created_at) = YEAR(CURRENT_DATE)   
                GROUP BY DATE_FORMAT(vi.created_at, '%Y-%m-%d')
                ORDER BY day DESC  
            """, nativeQuery = true)
    List<Map<String, Object>> countsMonth(@Param("vehicleId") Long vehicleId);

    /**
     * Counts the number of ignition events for a vehicle during the current year.
     *
     * @param vehicleId the ID of the vehicle for which to count ignition events.
     * @return a list of maps where each map contains:
     *         - "year" (Integer): the year for which the ignition events are counted.
     *         - "count" (Long): the number of ignition events during that year.
     *         Results are ordered by year in descending order.
     */
    @Query(value = """ 
                SELECT YEAR(vi.created_at) AS year, 
                       COUNT(vi.status) AS count 
                FROM vehicle_ignition vi 
                WHERE vi.vehicle_id = :vehicleId
                  AND vi.status = true
                  AND YEAR(vi.created_at) = YEAR(CURRENT_DATE)
                GROUP BY YEAR(vi.created_at)
                ORDER BY year DESC  
            """, nativeQuery = true)
    List<Map<String, Object>> countsYear(@Param("vehicleId") Long vehicleId);
}
