package com.pathpilot.ai.dto;

import java.util.List;

public record UserSummaryDto(
        Long id,
        String name,
        String email,
        String education,
        String branch,
        Integer graduationYear,
        String interests,
        List<String> roles
) {
}
