package com.pathpilot.ai.dto;

import java.util.List;

public record RoadmapDto(
        Long id,
        String title,
        String description,
        String careerName,
        List<RoadmapStepDto> steps
) {
}
