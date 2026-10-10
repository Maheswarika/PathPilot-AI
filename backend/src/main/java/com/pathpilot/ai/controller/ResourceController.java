package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.LearningResourceDto;
import com.pathpilot.ai.service.ResourceService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/resources")
    public List<LearningResourceDto> getResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/careers/{id}/resources")
    public List<LearningResourceDto> getCareerResources(
            @PathVariable Long id) {

        return resourceService.getResourcesForCareer(id);
    }
}