package com.pathpilot.ai.dto;

public record ProgressDto(
        Long roadmapId,
        String careerName,
        long completedSteps,
        long pendingSteps,
        double completionPercentage,
        String summary
) {
}
