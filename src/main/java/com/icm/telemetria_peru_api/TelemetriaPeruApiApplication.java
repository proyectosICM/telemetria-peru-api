package com.icm.telemetria_peru_api;

import com.icm.telemetria_peru_api.jobs.FuelTheftJobConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties({FuelTheftJobConfig.class})
@SpringBootApplication
public class TelemetriaPeruApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				TelemetriaPeruApiApplication.class, args);
	}
}
