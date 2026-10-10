package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.RecommendationDto;
import com.pathpilot.ai.dto.SkillGapDto;
import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.CareerRoleSkill;
import com.pathpilot.ai.entity.StudentSkill;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.repository.CareerRoleRepository;
import com.pathpilot.ai.repository.CareerRoleSkillRepository;
import com.pathpilot.ai.repository.StudentSkillRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RecommendationService {

    private final CareerRoleRepository careerRoleRepository;
    private final CareerRoleSkillRepository careerRoleSkillRepository;
    private final StudentSkillRepository studentSkillRepository;

    public RecommendationService(
            CareerRoleRepository careerRoleRepository,
            CareerRoleSkillRepository careerRoleSkillRepository,
            StudentSkillRepository studentSkillRepository) {

        this.careerRoleRepository = careerRoleRepository;
        this.careerRoleSkillRepository = careerRoleSkillRepository;
        this.studentSkillRepository = studentSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(User user) {

        Set<String> studentSkills = getStudentSkills(user);
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (CareerRole career : careerRoleRepository.findAll()) {

            List<CareerRoleSkill> requiredSkills =
                    careerRoleSkillRepository
                            .findByCareerRoleOrderBySequenceAsc(career);

            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (CareerRoleSkill relation : requiredSkills) {
                String skillName = relation.getSkill().getName();

                if (studentSkills.contains(normalize(skillName))) {
                    matched.add(skillName);
                } else {
                    missing.add(skillName);
                }
            }

            double skillScore = requiredSkills.isEmpty()
                    ? 0.0
                    : matched.size() * 100.0 / requiredSkills.size();

            double interestBonus = calculateInterestBonus(
                    user.getInterests(), career.getName());

            double score = Math.min(100.0, skillScore + interestBonus);

            String reason = "Matched " + matched.size() + " of "
                    + requiredSkills.size() + " required skills. "
                    + (matched.isEmpty()
                    ? "This career may be a learning opportunity."
                    : "Matched skills: " + String.join(", ", matched));

            recommendations.add(
                    new RecommendationDto(
                            career.getId(),
                            career.getName(),
                            score,
                            reason,
                            matched.isEmpty()
                                    ? "No direct matches yet"
                                    : String.join(", ", matched),
                            missing.isEmpty()
                                    ? "No skill gaps identified"
                                    : String.join(", ", missing)
                    )
            );
        }

        recommendations.sort(
                (first, second) ->
                        Double.compare(second.score(), first.score())
        );

        return recommendations;
    }

    @Transactional(readOnly = true)
    public SkillGapDto getSkillGap(User user, Long careerId) {

        CareerRole career = careerRoleRepository.findById(careerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Career role not found"
                ));

        Set<String> studentSkills = getStudentSkills(user);

        List<CareerRoleSkill> requiredSkills =
                careerRoleSkillRepository
                        .findByCareerRoleOrderBySequenceAsc(career);

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (CareerRoleSkill relation : requiredSkills) {
            String skillName = relation.getSkill().getName();

            if (studentSkills.contains(normalize(skillName))) {
                matched.add(skillName);
            } else {
                missing.add(skillName);
            }
        }

        double percentage = requiredSkills.isEmpty()
                ? 0.0
                : matched.size() * 100.0 / requiredSkills.size();

        String explanation =
                "Match percentage = matched required skills / total required skills × 100. "
                + "This measures skill overlap, not guaranteed job readiness.";

        return new SkillGapDto(
                career.getId(),
                career.getName(),
                matched,
                missing,
                percentage,
                explanation
        );
    }

    private Set<String> getStudentSkills(User user) {

        Set<String> skills = new HashSet<>();

        for (StudentSkill studentSkill :
                studentSkillRepository.findByUserOrderByIdAsc(user)) {

            if (studentSkill.getSkill() != null
                    && studentSkill.getSkill().getName() != null) {

                skills.add(normalize(studentSkill.getSkill().getName()));
            }
        }

        return skills;
    }

    private String normalize(String skillName) {
        return skillName.trim().toLowerCase(Locale.ROOT);
    }

    private double calculateInterestBonus(
            String interests, String careerName) {

        if (interests == null || interests.isBlank()
                || careerName == null) {
            return 0.0;
        }

        String interestText = normalize(interests);
        String roleName = normalize(careerName);

        if (roleName.contains("frontend")
                && interestText.contains("frontend")) {
            return 10.0;
        }

        if (roleName.contains("backend")
                && interestText.contains("backend")) {
            return 10.0;
        }

        if ((roleName.contains("ai")
                || roleName.contains("artificial intelligence"))
                && (interestText.contains("ai")
                || interestText.contains("artificial intelligence"))) {
            return 10.0;
        }

        if (roleName.contains("data")
                && interestText.contains("data")) {
            return 10.0;
        }

        return 0.0;
    }
}