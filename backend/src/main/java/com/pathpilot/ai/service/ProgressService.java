package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.ProgressDto;
import com.pathpilot.ai.entity.Roadmap;
import com.pathpilot.ai.entity.RoadmapStep;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.repository.RoadmapRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressService {

    private final RoadmapRepository roadmapRepository;

    public ProgressService(RoadmapRepository roadmapRepository) {
        this.roadmapRepository = roadmapRepository;
    }

    @Transactional(readOnly = true)
    public List<ProgressDto> getMyProgress(User user) {

        return roadmapRepository
                .findByUserOrderByGeneratedAtDesc(user)
                .stream()
                .map(this::toProgressDto)
                .toList();
    }

    private ProgressDto toProgressDto(Roadmap roadmap) {

        List<RoadmapStep> steps = roadmap.getSteps();

        long completedSteps = steps.stream()
                .filter(step -> Boolean.TRUE.equals(step.getCompleted()))
                .count();

        long pendingSteps = steps.size() - completedSteps;

        double completionPercentage = steps.isEmpty()
                ? 0.0
                : completedSteps * 100.0 / steps.size();

        String summary = completedSteps + " completed, "
                + pendingSteps + " pending";

        return new ProgressDto(
                roadmap.getId(),
                roadmap.getCareerRole().getName(),
                completedSteps,
                pendingSteps,
                completionPercentage,
                summary
        );
    }
}