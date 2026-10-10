package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.CareerRoleSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRoleSkillRepository extends JpaRepository<CareerRoleSkill, Long> {
    List<CareerRoleSkill> findByCareerRoleOrderBySequenceAsc(CareerRole careerRole);
}
