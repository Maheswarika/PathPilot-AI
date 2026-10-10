package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.Progress;
import com.pathpilot.ai.entity.Roadmap;
import com.pathpilot.ai.entity.RoadmapStep;
import com.pathpilot.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserAndRoadmap(User user, Roadmap roadmap);
    Optional<Progress> findByUserAndStep(User user, RoadmapStep step);
    long countByUserAndRoadmapAndCompleted(User user, Roadmap roadmap, boolean completed);
}
