package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.Skill;
import com.pathpilot.ai.entity.StudentSkill;
import com.pathpilot.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByUserOrderByIdAsc(User user);
    Optional<StudentSkill> findByUserAndSkill(User user, Skill skill);
    void deleteByUserIdAndSkillId(Long userId, Long skillId);
}
