package com.icm.telemetria_peru_api.integration.dvr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.DvrAlertExecuteRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class DvrCommandClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${CAMERAS_COMMAND_BASE_URL:http://telemetria-peru-cameras:7302}")
    private String camerasCommandBaseUrl;

    @Value("${CAMERAS_COMMAND_API_TOKEN:}")
    private String camerasCommandApiToken;

    public JsonNode getAlerts(String normalizedPhone) {
        String encodedPhone = URLEncoder.encode(normalizedPhone, StandardCharsets.UTF_8);
        HttpRequest request = baseRequest("/dvr-alerts?phone=" + encodedPhone).GET().build();
        return send(request);
    }

    public JsonNode execute(String normalizedPhone, DvrAlertExecuteRequestDTO requestDto) {
        try {
            String body = objectMapper.writeValueAsString(new ExecutePayload(
                    normalizedPhone,
                    requestDto.getAlertCode(),
                    requestDto.getSubalertCode(),
                    requestDto.getChannel(),
                    requestDto.getDurationSeconds()
            ));

            HttpRequest request = baseRequest("/dvr-alerts/execute")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return send(request);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo serializar la ejecucion DVR", e);
        }
    }

    private HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(camerasCommandBaseUrl + path))
                .header(HttpHeaders.CONTENT_TYPE, "application/json");
        if (camerasCommandApiToken != null && !camerasCommandApiToken.isBlank()) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + camerasCommandApiToken);
        }
        return builder;
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            if (response.statusCode() >= 400) {
                throw new RuntimeException(body.path("error").asText("Error DVR HTTP " + response.statusCode()));
            }
            return body;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("No se pudo comunicar con cameras", e);
        }
    }

    private record ExecutePayload(
            String phone,
            String alertCode,
            String subalertCode,
            Integer channel,
            Integer durationSeconds
    ) {
    }
}
