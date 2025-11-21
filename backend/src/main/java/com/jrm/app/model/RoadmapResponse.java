package com.jrm.app.model;

import java.util.List;

public class RoadmapResponse {
    private String targetRole;
    private int totalWeeks;
    private int dailyHours;
    private List<String> keywords;
    private List<RoadmapStep> steps;

    public RoadmapResponse(String targetRole, int totalWeeks, int dailyHours, List<String> keywords, List<RoadmapStep> steps) {
        this.targetRole = targetRole;
        this.totalWeeks = totalWeeks;
        this.dailyHours = dailyHours;
        this.keywords = keywords;
        this.steps = steps;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public int getDailyHours() {
        return dailyHours;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<RoadmapStep> getSteps() {
        return steps;
    }
}
