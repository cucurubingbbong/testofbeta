package com.jrm.app.controller;

import com.jrm.app.model.RoadmapRequest;
import com.jrm.app.model.RoadmapResponse;
import com.jrm.app.service.RoadmapEngine;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@CrossOrigin
public class RoadmapController {

    private final RoadmapEngine roadmapEngine;

    public RoadmapController(RoadmapEngine roadmapEngine) {
        this.roadmapEngine = roadmapEngine;
    }

    @PostMapping("/from-jd")
    public ResponseEntity<RoadmapResponse> fromJd(@Valid @RequestBody RoadmapRequest request) {
        return ResponseEntity.ok(roadmapEngine.generate(request));
    }
}
