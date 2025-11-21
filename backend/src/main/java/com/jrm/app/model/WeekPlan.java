package com.jrm.app.model;

import java.util.List;

public class WeekPlan {
    private int weekNumber;
    private List<String> topics;
    private String mission;

    public WeekPlan(int weekNumber, List<String> topics, String mission) {
        this.weekNumber = weekNumber;
        this.topics = topics;
        this.mission = mission;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getMission() {
        return mission;
    }
}
