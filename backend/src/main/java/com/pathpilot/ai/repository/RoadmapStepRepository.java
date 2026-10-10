package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.Roadmap;
import com.pathpilot.ai.entity.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {
    List<RoadmapStep> findByRoadmapOrderBySequenceAsc(Roadmap roadmap);
}
