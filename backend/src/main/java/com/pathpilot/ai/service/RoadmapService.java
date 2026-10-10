package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.LearningResourceDto;
import com.pathpilot.ai.dto.RoadmapDto;
import com.pathpilot.ai.dto.RoadmapStepDto;
import com.pathpilot.ai.entity.*;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final CareerRoleSkillRepository careerRoleSkillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ProgressRepository progressRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final SkillRepository skillRepository;

    public RoadmapService(RoadmapRepository roadmapRepository,
                         RoadmapStepRepository roadmapStepRepository,
                         CareerRoleRepository careerRoleRepository,
                         CareerRoleSkillRepository careerRoleSkillRepository,
                         StudentSkillRepository studentSkillRepository,
                         ProgressRepository progressRepository,
                         LearningResourceRepository learningResourceRepository,
                         SkillRepository skillRepository) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapStepRepository = roadmapStepRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.careerRoleSkillRepository = careerRoleSkillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.progressRepository = progressRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public RoadmapDto generateForUser(User user, Long careerId) {
        CareerRole careerRole = careerRoleRepository.findById(careerId)
                .orElseThrow(() -> new ApiException("Career not found.", HttpStatus.NOT_FOUND));

        Roadmap existing = roadmapRepository.findByUserAndCareerRole(user, careerRole).orElse(null);
        if (existing != null) {
            return toDto(existing);
        }

        Set<Long> knownSkillIds = studentSkillRepository.findByUserOrderByIdAsc(user).stream()
                .map(ss -> ss.getSkill().getId())
                .collect(Collectors.toSet());

        Roadmap roadmap = new Roadmap(user, careerRole, careerRole.getName() + " Roadmap", "Suggested plan to build the skills for " + careerRole.getName() + ".");
        roadmap = roadmapRepository.save(roadmap);

        List<CareerRoleSkill> requiredSkills = careerRoleSkillRepository.findByCareerRoleOrderBySequenceAsc(careerRole);
        List<RoadmapStep> steps = new ArrayList<>();
        int sequence = 1;
        for (CareerRoleSkill relation : requiredSkills) {
            Skill skill = relation.getSkill();
            if (knownSkillIds.contains(skill.getId())) {
                continue;
            }

            LearningResource resource = learningResourceRepository.findByCareerRoleOrderByIdAsc(careerRole).stream()
                    .filter(r -> r.getSkill() != null && r.getSkill().getId().equals(skill.getId()))
                    .findFirst()
                    .orElse(null);

            RoadmapStep step = new RoadmapStep(
                    roadmap,
                    skill,
                    sequence,
                    "Learn " + skill.getName(),
                    "Build confidence in " + skill.getName() + " and apply it in a small project or exercise.",
                    resource
            );
            step.setCompleted(false);
            steps.add(roadmapStepRepository.save(step));
            sequence++;
        }

        if (steps.isEmpty()) {
            Skill fallbackSkill = requiredSkills.isEmpty() ? new Skill("Career readiness", "General") : requiredSkills.get(0).getSkill();
            if (fallbackSkill.getId() == null) {
                Skill fallbackSkillToSave = fallbackSkill;
                fallbackSkill = skillRepository.findByNameIgnoreCase(fallbackSkill.getName())
                        .orElseGet(() -> skillRepository.save(fallbackSkillToSave));
            }
            RoadmapStep step = new RoadmapStep(
                    roadmap,
                    fallbackSkill,
                    1,
                    "Review your current strengths",
                    "You already match the core requirements for this career, so continue to refine your portfolio and practice interviews.",
                    null
            );
            step.setCompleted(true);
            steps.add(roadmapStepRepository.save(step));
        }

        roadmap.setSteps(steps);
        return toDto(roadmapRepository.save(roadmap));
    }

    @Transactional(readOnly = true)
    public List<RoadmapDto> getMyRoadmaps(User user) {
        return roadmapRepository.findByUserOrderByGeneratedAtDesc(user).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RoadmapDto getRoadmap(User user, Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ApiException("Roadmap not found.", HttpStatus.NOT_FOUND));
        if (!roadmap.getUser().getId().equals(user.getId())) {
            throw new ApiException("You do not have access to this roadmap.", HttpStatus.FORBIDDEN);
        }
        return toDto(roadmap);
    }

    @Transactional
    public RoadmapStepDto completeStep(User user, Long roadmapId, Long stepId, boolean completed) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ApiException("Roadmap not found.", HttpStatus.NOT_FOUND));
        if (!roadmap.getUser().getId().equals(user.getId())) {
            throw new ApiException("You do not have access to this roadmap.", HttpStatus.FORBIDDEN);
        }

        RoadmapStep step = roadmapStepRepository.findById(stepId)
                .orElseThrow(() -> new ApiException("Roadmap step not found.", HttpStatus.NOT_FOUND));
        if (!step.getRoadmap().getId().equals(roadmap.getId())) {
            throw new ApiException("The selected step does not belong to this roadmap.", HttpStatus.BAD_REQUEST);
        }

        step.setCompleted(completed);
        RoadmapStep savedStep = roadmapStepRepository.save(step);

        Progress progress = progressRepository.findByUserAndStep(user, savedStep)
                .orElse(new Progress(user, roadmap, savedStep));
        progress.setCompleted(completed);
        progress.setCompletedAt(completed ? LocalDateTime.now() : null);
        progressRepository.save(progress);

        return toStepDto(savedStep);
    }

    private RoadmapDto toDto(Roadmap roadmap) {
        return new RoadmapDto(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getDescription(),
                roadmap.getCareerRole() != null ? roadmap.getCareerRole().getName() : "Career",
                roadmapStepRepository.findByRoadmapOrderBySequenceAsc(roadmap).stream().map(this::toStepDto).toList()
        );
    }

    private RoadmapStepDto toStepDto(RoadmapStep step) {
        LearningResourceDto resourceDto = null;
        if (step.getLearningResource() != null) {
            LearningResource resource = step.getLearningResource();
            resourceDto = new LearningResourceDto(
                    resource.getId(),
                    resource.getTitle(),
                    resource.getDescription(),
                    resource.getType(),
                    resource.getUrl(),
                    resource.getCareerRole() != null ? resource.getCareerRole().getName() : null,
                    resource.getSkill() != null ? resource.getSkill().getName() : null
            );
        }
        return new RoadmapStepDto(
                step.getId(),
                step.getTitle(),
                step.getDescription(),
                step.getSequence(),
                step.getSkill() != null ? step.getSkill().getName() : null,
                step.getCompleted(),
                resourceDto
        );
    }
}
