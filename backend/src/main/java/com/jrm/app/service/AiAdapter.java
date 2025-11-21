package com.jrm.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AiAdapter {
    private static final Logger log = LoggerFactory.getLogger(AiAdapter.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl;
    private final String apiKey;

    public AiAdapter(
            @Value("${ai.api.url:}") String apiUrl,
            @Value("${ai.api.key:}") String apiKey
    ) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public List<String> summarizeTopics(List<String> topics) {
        if (hasExternalConfig()) {
            try {
                return callExternalApi(topics);
            } catch (Exception ex) {
                log.warn("AI API 호출에 실패했습니다. 기본 미션으로 대체합니다.", ex);
            }
        }
        return fallbackMissions(topics);
    }

    private boolean hasExternalConfig() {
        return apiUrl != null && !apiUrl.isBlank() && apiKey != null && !apiKey.isBlank();
    }

    private List<String> callExternalApi(List<String> topics) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Map<String, Object> payload = Collections.singletonMap("topics", topics);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        Map<?, ?> response = restTemplate.postForObject(apiUrl, entity, Map.class);
        if (response != null && response.containsKey("missions")) {
            Object missions = response.get("missions");
            if (missions instanceof List<?>) {
                return ((List<?>) missions).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        }
        return fallbackMissions(topics);
    }

    private List<String> fallbackMissions(List<String> topics) {
        return topics.stream()
                .map(t -> "실습 미션: " + t + " 중심으로 구현")
                .collect(Collectors.toList());
    }
}
