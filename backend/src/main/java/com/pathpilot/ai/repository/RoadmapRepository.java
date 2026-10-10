package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.Roadmap;
import com.pathpilot.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByUserOrderByGeneratedAtDesc(User user);
    Optional<Roadmap> findByUserAndCareerRole(User user, CareerRole careerRole);
}
