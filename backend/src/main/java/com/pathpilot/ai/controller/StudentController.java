package com.pathpilot.ai.controller;

import com.pathpilot.ai.dto.ProfileUpdateRequest;
import com.pathpilot.ai.dto.SkillDto;
import com.pathpilot.ai.dto.UserSummaryDto;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.repository.SkillRepository;
import com.pathpilot.ai.repository.UserRepository;
import com.pathpilot.ai.service.StudentSkillService;
import com.pathpilot.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StudentController {
    private final StudentSkillService studentSkillService;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public StudentController(StudentSkillService studentSkillService,
                            SkillRepository skillRepository,
                            UserRepository userRepository,
                            UserService userService) {
        this.studentSkillService = studentSkillService;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/skills")
    public List<SkillDto> getSkills() {
        return skillRepository.findAll().stream()
                .map(skill -> new SkillDto(skill.getId(), skill.getName(), skill.getCategory()))
                .toList();
    }

    @GetMapping("/students/me/skills")
    public List<SkillDto> getMySkills(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return studentSkillService.getMySkills(user);
    }

    @PostMapping("/students/me/skills")
    public ResponseEntity<SkillDto> addMySkill(Authentication authentication, @RequestBody Map<String, String> payload) {
        String skillName = payload.getOrDefault("skillName", payload.getOrDefault("name", ""));
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(studentSkillService.addSkill(user, skillName));
    }

    @DeleteMapping("/students/me/skills/{skillId}")
    public ResponseEntity<Void> deleteMySkill(Authentication authentication, @PathVariable Long skillId) {
        User user = userService.getCurrentUser(authentication);
        studentSkillService.deleteSkill(user, skillId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/me/profile")
    public ResponseEntity<UserSummaryDto> getProfile(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(userService.mapToSummary(user));
    }

    @PutMapping("/students/me/profile")
    public ResponseEntity<UserSummaryDto> updateProfile(Authentication authentication, @RequestBody ProfileUpdateRequest request) {
        User user = userService.getCurrentUser(authentication);
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.education() != null) {
            user.setEducation(request.education());
        }
        if (request.branch() != null) {
            user.setBranch(request.branch());
        }
        if (request.graduationYear() != null) {
            user.setGraduationYear(request.graduationYear());
        }
        if (request.interests() != null) {
            user.setInterests(request.interests());
        }
        userRepository.save(user);
        return ResponseEntity.ok(userService.mapToSummary(user));
    }
}
