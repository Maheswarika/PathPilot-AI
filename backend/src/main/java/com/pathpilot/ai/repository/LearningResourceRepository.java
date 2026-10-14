package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.LearningResource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository
        extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findByCareerRoleOrderByIdAsc(
            CareerRole careerRole);

    boolean existsByTitleAndCareerRole(
            String title,
            CareerRole careerRole);
}