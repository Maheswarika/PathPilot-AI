package com.pathpilot.ai.dto;

public record RecommendationDto(
        Long careerId,
        String careerName,
        double score,
        String recommendationReason,
        String matchedSkills,
        String missingSkills
) {
}
