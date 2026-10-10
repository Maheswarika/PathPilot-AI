package com.pathpilot.ai.dto;

public record RoadmapStepDto(
        Long id,
        String title,
        String description,
        Integer sequence,
        String skillName,
        Boolean completed,
        LearningResourceDto resource
) {
}
