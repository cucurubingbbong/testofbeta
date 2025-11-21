package com.jrm.app.service;

import com.jrm.app.model.RoadmapRequest;
import com.jrm.app.model.RoadmapResponse;
import com.jrm.app.model.RoadmapStep;
import com.jrm.app.model.WeekPlan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoadmapEngine {

    private static final Map<String, List<String>> PREREQUISITES = Map.of(
            "spring", List.of("Java OOP", "HTTP", "MVC", "Database basics"),
            "react", List.of("JavaScript ES6", "DOM", "HTML/CSS", "State management"),
            "aws", List.of("Linux CLI", "Networking", "Deployment pipeline"),
            "mysql", List.of("SQL", "Indexes", "Transactions"),
            "docker", List.of("Containers", "Images", "Compose")
    );

    public RoadmapResponse generate(RoadmapRequest request) {
        List<String> keywords = extractKeywords(request.getJdText());
        List<RoadmapStep> steps = new ArrayList<>();

        steps.add(new RoadmapStep(
                "Step 1. 선수지식",
                2,
                List.of(new WeekPlan(1, List.of("자료구조/알고리즘 기초", "운영체제, 네트워크 핵심"), "CS50/모던CS 요약 강의 시청"),
                        new WeekPlan(2, List.of("Git/GitHub", "리눅스 CLI", "HTTP/REST"), "간단한 CRUD API 만들기"))
        ));

        steps.add(new RoadmapStep(
                "Step 2. 핵심기술",
                4,
                keywords.stream()
                        .map(k -> new WeekPlan(steps.stream().mapToInt(RoadmapStep::getEstimatedWeeks).sum() + (int) (Math.random() * 2 + 1),
                                PREREQUISITES.getOrDefault(k, List.of(k + " 기본")),
                                k.toUpperCase(Locale.ROOT) + " 튜토리얼 따라 만들기"))
                        .collect(Collectors.toList())
        ));

        steps.add(new RoadmapStep(
                "Step 3. 실무스택",
                4,
                List.of(
                        new WeekPlan(1, List.of("테스트 코드", "CI 파이프라인"), "GitHub Actions로 빌드/테스트 자동화"),
                        new WeekPlan(2, List.of("보안/인증", "모니터링"), "JWT 로그인 + 로그 수집"),
                        new WeekPlan(3, List.of("배포 자동화", "클라우드"), "Docker + 클라우드 배포"),
                        new WeekPlan(4, List.of("팀 협업"), "칸반/이슈 기반 협업 시뮬레이션")
                )
        ));

        steps.add(new RoadmapStep(
                "Step 4. 보너스스킬",
                2,
                List.of(
                        new WeekPlan(1, List.of("오픈소스 기여", "기술 블로그"), "PR 1건 + 블로그 1편"),
                        new WeekPlan(2, List.of("인터뷰 준비", "CS 심화"), "모의 면접 2회 진행")
                )
        ));

        int totalWeeks = steps.stream().mapToInt(RoadmapStep::getEstimatedWeeks).sum();
        return new RoadmapResponse("Custom JD 기반 로드맵", totalWeeks, request.getDailyHours(), steps);
    }

    private List<String> extractKeywords(String jdText) {
        String lowered = jdText.toLowerCase(Locale.ROOT);
        List<String> candidates = new ArrayList<>();
        PREREQUISITES.keySet().forEach(key -> {
            if (lowered.contains(key)) {
                candidates.add(key);
            }
        });
        if (candidates.isEmpty()) {
            candidates = Arrays.asList("java", "spring", "aws");
        }
        return candidates;
    }
}
