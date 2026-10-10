package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.ProgressDto;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.service.ProgressService;
import com.pathpilot.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProgressController {
    private final ProgressService progressService;
    private final UserService userService;

    public ProgressController(ProgressService progressService, UserService userService) {
        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping("/progress/me")
    public ResponseEntity<List<ProgressDto>> myProgress(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(progressService.getMyProgress(user));
    }
}
