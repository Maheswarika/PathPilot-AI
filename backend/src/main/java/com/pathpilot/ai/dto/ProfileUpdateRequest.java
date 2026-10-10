package com.pathpilot.ai.dto;

public record ProfileUpdateRequest(
        String name,
        String education,
        String branch,
        Integer graduationYear,
        String interests
) {
}
