package com.jrm.app.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiAdapter {
    public List<String> summarizeTopics(List<String> topics) {
        // placeholder for external AI API call: here we simply join topics
        return topics.stream()
                .map(t -> "실습 미션: " + t + " 중심으로 구현")
                .collect(Collectors.toList());
    }
}
