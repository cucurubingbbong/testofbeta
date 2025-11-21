package com.jrm.app.service;

import com.jrm.app.model.RoadmapRecord;
import com.jrm.app.model.RoadmapResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoadmapStore {
    private final Map<String, List<RoadmapRecord>> data = new ConcurrentHashMap<>();

    public RoadmapRecord save(String email, String title, int progress, RoadmapResponse roadmap) {
        RoadmapRecord record = new RoadmapRecord(title, progress, roadmap);
        data.computeIfAbsent(email, k -> new ArrayList<>()).add(record);
        return record;
    }

    public List<RoadmapRecord> list(String email) {
        return data.getOrDefault(email, List.of());
    }
}
