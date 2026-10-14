
package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.AuthResponse;
import com.pathpilot.ai.dto.LoginRequest;
import com.pathpilot.ai.dto.RegisterRequest;
import com.pathpilot.ai.dto.UserSummaryDto;
import com.pathpilot.ai.entity.Role;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.RoleRepository;
import com.pathpilot.ai.repository.UserRepository;
import com.pathpilot.ai.security.JwtService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Register a new user
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(
                    "Passwords do not match.",
                    HttpStatus.BAD_REQUEST
            );
        }

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ApiException(
                    "A user with this email already exists.",
                    HttpStatus.CONFLICT
            );
        }

        User user = new User(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password())
        );

        // Public registration only assigns the student role.
        Role studentRole = roleRepository
                .findByName(Role.RoleName.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(
                        new Role(Role.RoleName.ROLE_STUDENT)
                ));

        user.setRoles(Set.of(studentRole));

        User savedUser;

        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            if (isUniqueConstraintViolation(ex)) {
                throw new ApiException(
                        "A user with this email already exists.",
                        HttpStatus.CONFLICT
                );
            }

            throw ex;
        }

        return buildAuthResponse(savedUser);
    }

    // Authenticate an existing user
    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new ApiException(
                    "Invalid email or password.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(
                        "Invalid email or password.",
                        HttpStatus.UNAUTHORIZED
                ));

        return buildAuthResponse(user);
    }

    // Return the currently authenticated user's details
    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUserDto(
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        return mapToSummary(user);
    }

    // Find the currently authenticated user
    @Transactional(readOnly = true)
    public User getCurrentUser(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {

            throw new ApiException(
                    "Authentication is required.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return findByEmail(authentication.getName());
    }

    // Find a user by email
    @Transactional(readOnly = true)
    public User findByEmail(String email) {

        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ApiException(
                        "User not found.",
                        HttpStatus.NOT_FOUND
                ));
    }

    // Convert user entity to a safe response DTO
    public UserSummaryDto mapToSummary(User user) {

        return new UserSummaryDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEducation(),
                user.getBranch(),
                user.getGraduationYear(),
                user.getInterests(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .toList()
        );
    }

    // Generate a JWT and create the authentication response
    private AuthResponse buildAuthResponse(User user) {

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                mapToSummary(user)
        );
    }

    // Normalize email addresses consistently
    private String normalizeEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Check whether the database rejected a duplicate unique value
    private boolean isUniqueConstraintViolation(Throwable error) {

        for (Throwable cause = error;
             cause != null;
             cause = cause.getCause()) {

            if (cause instanceof SQLException sqlException
                    && (sqlException.getErrorCode() == 1062
                    || "23505".equals(sqlException.getSQLState()))) {

                return true;
            }
        }

        return false;
    }
}
