package com.icm.telemetria_peru_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS) in the application.
 * This configuration enables CORS, allowing specific HTTP methods and headers to be accessed
 * from any origin, making the application accessible from various domains.
 *
 * <p>
 * The `addCorsMappings` method defines the CORS policy, specifying which endpoints are exposed
 * to cross-origin requests. The configuration allows requests from any origin (`allowedOrigins("*")`),
 * and permits specific HTTP methods (`GET`, `POST`, `PUT`) to be used. It also allows all headers
 * (`allowedHeaders("*")`) and sets a cache period of 3600 seconds (`maxAge(3600)`).
 * </p>
 *
 * <p>
 * This configuration is useful in applications that need to interact with frontend clients
 * from different domains, ensuring secure and efficient communication between backend APIs
 * and frontend applications.
 * </p>
 */
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
