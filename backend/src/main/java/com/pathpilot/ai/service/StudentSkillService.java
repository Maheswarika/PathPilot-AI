package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.SkillDto;
import com.pathpilot.ai.entity.Skill;
import com.pathpilot.ai.entity.StudentSkill;
import com.pathpilot.ai.entity.User;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.SkillRepository;
import com.pathpilot.ai.repository.StudentSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentSkillService {
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;

    public StudentSkillService(StudentSkillRepository studentSkillRepository, SkillRepository skillRepository) {
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getMySkills(User user) {
        List<SkillDto> response = new ArrayList<>();
        for (StudentSkill studentSkill : studentSkillRepository.findByUserOrderByIdAsc(user)) {
            response.add(new SkillDto(studentSkill.getSkill().getId(), studentSkill.getSkill().getName(), studentSkill.getSkill().getCategory()));
        }
        return response;
    }

    @Transactional
    public SkillDto addSkill(User user, String skillName) {
        String trimmed = skillName == null ? "" : skillName.trim();
        if (trimmed.isBlank()) {
            throw new ApiException("Skill name is required.", HttpStatus.BAD_REQUEST);
        }

        Skill skill = skillRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> skillRepository.save(new Skill(trimmed, "General")));

        StudentSkill existing = studentSkillRepository.findByUserAndSkill(user, skill).orElse(null);
        if (existing != null) {
            return new SkillDto(skill.getId(), skill.getName(), skill.getCategory());
        }

        StudentSkill saved = studentSkillRepository.save(new StudentSkill(user, skill, 1));
        return new SkillDto(saved.getSkill().getId(), saved.getSkill().getName(), saved.getSkill().getCategory());
    }

    @Transactional
    public void deleteSkill(User user, Long skillId) {
        studentSkillRepository.deleteByUserIdAndSkillId(user.getId(), skillId);
    }
}
