package com.jrm.app.controller;

import com.jrm.app.model.CommunityPost;
import com.jrm.app.service.AuthService;
import com.jrm.app.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin
public class CommunityController {
    private final CommunityService communityService;
    private final AuthService authService;

    public CommunityController(CommunityService communityService, AuthService authService) {
        this.communityService = communityService;
        this.authService = authService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPost>> posts() {
        return ResponseEntity.ok(communityService.getPosts());
    }

    @PostMapping("/posts")
    public ResponseEntity<CommunityPost> create(@RequestHeader("X-Auth-Token") String token,
                                                @RequestBody Map<String, String> payload) {
        String email = authService.getEmail(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        CommunityPost post = communityService.add(email,
                payload.getOrDefault("title", "공유"),
                payload.getOrDefault("content", ""));
        return ResponseEntity.ok(post);
    }
}
