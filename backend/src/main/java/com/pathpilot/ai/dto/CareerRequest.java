package com.pathpilot.ai.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CareerRequest(
        @NotBlank(message = "Career name is required")
        String name,
        String description,
        String targetAudience,
        List<String> requiredSkillNames
) {
}
