package com.pathpilot.ai.dto;

import java.util.List;

public record SkillGapDto(
        Long careerId,
        String careerName,
        List<String> matchedSkills,
        List<String> missingSkills,
        double matchPercentage,
        String explanation
) {
}
