package com.jrm.app.controller;

import com.jrm.app.model.RoadmapRecord;
import com.jrm.app.model.SaveRoadmapRequest;
import com.jrm.app.service.AuthService;
import com.jrm.app.service.RoadmapStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {
    private final AuthService authService;
    private final RoadmapStore roadmapStore;

    public ProfileController(AuthService authService, RoadmapStore roadmapStore) {
        this.authService = authService;
        this.roadmapStore = roadmapStore;
    }

    @PostMapping("/roadmaps")
    public ResponseEntity<RoadmapRecord> save(@RequestHeader("X-Auth-Token") String token,
                                               @RequestBody SaveRoadmapRequest payload) {
        String email = authService.getEmail(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        String title = payload.getTitle() == null ? "나의 로드맵" : payload.getTitle();
        int progress = payload.getProgress() == null ? 0 : payload.getProgress();
        RoadmapRecord record = roadmapStore.save(email, title, progress, payload.getRoadmap());
        return ResponseEntity.ok(record);
    }

    @GetMapping("/roadmaps")
    public ResponseEntity<List<RoadmapRecord>> list(@RequestHeader("X-Auth-Token") String token) {
        String email = authService.getEmail(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(roadmapStore.list(email));
    }
}
