package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.CareerDto;
import com.pathpilot.ai.dto.CareerSkillDto;
import com.pathpilot.ai.dto.SkillGapDto;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.service.CareerService;
import com.pathpilot.ai.service.RecommendationService;
import com.pathpilot.ai.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    private final CareerService careerService;
    private final RecommendationService recommendationService;
    private final UserService userService;

    public CareerController(
            CareerService careerService,
            RecommendationService recommendationService,
            UserService userService) {

        this.careerService = careerService;
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    // Get all careers
    @GetMapping
    public List<CareerDto> getCareers() {
        return careerService.getAllCareers();
    }

    // Get one career by ID
    @GetMapping("/{id}")
    public CareerDto getCareer(@PathVariable Long id) {
        return careerService.getCareerDto(id);
    }

    // Get skills required for a career
    @GetMapping("/{id}/skills")
    public List<CareerSkillDto> getCareerSkills(
            @PathVariable Long id) {

        return careerService.getCareerSkills(id);
    }

    // Get the current user's skill gap for a career
    @GetMapping("/{id}/skill-gap")
    public SkillGapDto getSkillGap(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getCurrentUser(authentication);

        return recommendationService.getSkillGap(user, id);
    }
}