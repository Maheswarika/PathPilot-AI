package com.pathpilot.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathpilot.ai.dto.AuthResponse;
import com.pathpilot.ai.dto.LoginRequest;
import com.pathpilot.ai.dto.RegisterRequest;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.UserRepository;
import com.pathpilot.ai.security.JwtService;
import com.pathpilot.ai.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String email;

    @BeforeEach
    void setUp() {
        email = "student-" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void registerAndLoginFlowWorks() {
        AuthResponse response = userService.register(
                new RegisterRequest("Test Student", email, "secret123", "secret123")
        );

        assertNotNull(response);
        assertTrue(jwtService.isTokenValid(response.token()));
        assertEquals(email, response.user().email());

        User savedUser = userRepository.findByEmail(email).orElseThrow();
        assertNotEquals("secret123", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("secret123", savedUser.getPassword()));

        AuthResponse loginResponse = userService.login(
                new LoginRequest(email.toUpperCase(), "secret123")
        );
        assertTrue(jwtService.isTokenValid(loginResponse.token()));
        assertEquals("Test Student", loginResponse.user().name());
    }

    @Test
    void duplicateRegistrationIsRejectedWithoutCreatingAnotherUser() {
        userService.register(new RegisterRequest("Test Student", email, "secret123", "secret123"));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> userService.register(
                        new RegisterRequest("Another Student", email.toUpperCase(), "secret456", "secret456")
                )
        );

        assertEquals(409, exception.getStatus().value());
        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(1, userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .count());
    }

    @Test
    void invalidPasswordIsRejected() {
        userService.register(new RegisterRequest("Test Student", email, "secret123", "secret123"));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> userService.login(new LoginRequest(email, "incorrect-password"))
        );

        assertEquals(401, exception.getStatus().value());
        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    void registrationEndpointPersistsUserAndJwtProtectsStudentEndpoint() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new RegisterRequest("HTTP Student", email, "secret123", "secret123")
        );
        String responseBody = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(responseBody).get("token").asText();
        assertTrue(userRepository.findByEmail(email).isPresent());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("Duplicate Student", email.toUpperCase(), "secret123", "secret123")
                        )))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exists."));

        mockMvc.perform(get("/api/students/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(get("/api/students/me/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginEndpointReturnsJwtForValidCredentialsAndRejectsInvalidPassword() throws Exception {
        userService.register(new RegisterRequest("HTTP Student", email, "secret123", "secret123"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    void corsAllowsViteDevelopmentOrigin() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", "http://localhost:5174")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"));
    }

    @Test
    void registrationEndpointReturnsValidationErrors() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("Student", "not-an-email", "secret123", "secret123")
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Enter a valid email"));
    }
}
