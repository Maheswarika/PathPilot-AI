
package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.AuthResponse;
import com.pathpilot.ai.dto.LoginRequest;
import com.pathpilot.ai.dto.RegisterRequest;
import com.pathpilot.ai.dto.UserSummaryDto;
import com.pathpilot.ai.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getCurrentUser(
            Authentication authentication) {

        UserSummaryDto user =
                userService.getCurrentUserDto(authentication);

        return ResponseEntity.ok(user);
    }
}
