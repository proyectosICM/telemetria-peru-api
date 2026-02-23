package com.icm.telemetria_peru_api.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartbeatJob {
    @Scheduled(cron ="0 0 10 * * *")
    public void run() {
        log.warn("[JOB-HEARTBEAT] Scheduler activo: HeartbeatJob ejecutado a las 10:00");
    }
}
