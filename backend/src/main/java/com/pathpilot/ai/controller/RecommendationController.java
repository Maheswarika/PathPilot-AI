package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.RecommendationDto;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.service.RecommendationService;
import com.pathpilot.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(recommendationService.getRecommendations(user));
    }
}
