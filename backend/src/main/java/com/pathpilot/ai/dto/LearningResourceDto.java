package com.pathpilot.ai.dto;

public record LearningResourceDto(
        Long id,
        String title,
        String description,
        String type,
        String url,
        String careerName,
        String skillName
) {
}
