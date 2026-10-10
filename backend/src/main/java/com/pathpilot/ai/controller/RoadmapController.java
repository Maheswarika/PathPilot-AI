package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.RoadmapDto;
import com.pathpilot.ai.dto.RoadmapStepDto;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.service.RoadmapService;
import com.pathpilot.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
public class RoadmapController {
    private final RoadmapService roadmapService;
    private final UserService userService;

    public RoadmapController(RoadmapService roadmapService, UserService userService) {
        this.roadmapService = roadmapService;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RoadmapDto> generateRoadmap(Authentication authentication, @RequestParam Long careerId) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(roadmapService.generateForUser(user, careerId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RoadmapDto>> getMyRoadmaps(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(roadmapService.getMyRoadmaps(user));
    }

    @GetMapping("/me/{roadmapId}")
    public ResponseEntity<RoadmapDto> getRoadmap(Authentication authentication, @PathVariable Long roadmapId) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(roadmapService.getRoadmap(user, roadmapId));
    }

    @PatchMapping("/me/{roadmapId}/steps/{stepId}/complete")
    public ResponseEntity<RoadmapStepDto> completeStep(Authentication authentication,
                                                      @PathVariable Long roadmapId,
                                                      @PathVariable Long stepId,
                                                      @RequestParam(defaultValue = "true") boolean completed) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(roadmapService.completeStep(user, roadmapId, stepId, completed));
    }
}
