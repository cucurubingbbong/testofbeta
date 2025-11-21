package com.jrm.app.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RoadmapRecord {
    private final String id;
    private final String title;
    private final LocalDate createdAt;
    private final int progress;
    private final RoadmapResponse roadmap;

    public RoadmapRecord(String title, int progress, RoadmapResponse roadmap) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.progress = progress;
        this.roadmap = roadmap;
        this.createdAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public int getProgress() {
        return progress;
    }

    public RoadmapResponse getRoadmap() {
        return roadmap;
    }
}
