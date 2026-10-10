package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.CareerDto;
import com.pathpilot.ai.dto.CareerRequest;
import com.pathpilot.ai.dto.LearningResourceDto;
import com.pathpilot.ai.dto.ResourceRequest;
import com.pathpilot.ai.service.CareerService;
import com.pathpilot.ai.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final CareerService careerService;
    private final ResourceService resourceService;

    public AdminController(CareerService careerService, ResourceService resourceService) {
        this.careerService = careerService;
        this.resourceService = resourceService;
    }

    @PostMapping("/careers")
    public ResponseEntity<CareerDto> createCareer(@Valid @RequestBody CareerRequest request) {
        return ResponseEntity.ok(careerService.createCareer(request));
    }

    @PutMapping("/careers/{id}")
    public ResponseEntity<CareerDto> updateCareer(@PathVariable Long id, @Valid @RequestBody CareerRequest request) {
        return ResponseEntity.ok(careerService.updateCareer(id, request));
    }

    @DeleteMapping("/careers/{id}")
    public ResponseEntity<Void> deleteCareer(@PathVariable Long id) {
        careerService.deleteCareer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resources")
    public ResponseEntity<LearningResourceDto> createResource(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(resourceService.createResource(request));
    }

    @PutMapping("/resources/{id}")
    public ResponseEntity<LearningResourceDto> updateResource(@PathVariable Long id, @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateResource(id, request));
    }

    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
