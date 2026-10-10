package com.pathpilot.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record ResourceRequest(
        @NotBlank(message = "Resource title is required")
        String title,
        String description,
        @NotBlank(message = "Resource type is required")
        String type,
        @NotBlank(message = "URL is required")
        String url,
        String skillName,
        Long careerId
) {
}
