package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.CareerDto;
import com.pathpilot.ai.dto.CareerRequest;
import com.pathpilot.ai.dto.CareerSkillDto;
import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.CareerRoleSkill;
import com.pathpilot.ai.entity.Skill;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.CareerRoleRepository;
import com.pathpilot.ai.repository.CareerRoleSkillRepository;
import com.pathpilot.ai.repository.LearningResourceRepository;
import com.pathpilot.ai.repository.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CareerService {
    private final CareerRoleRepository careerRoleRepository;
    private final CareerRoleSkillRepository careerRoleSkillRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final SkillRepository skillRepository;

    public CareerService(CareerRoleRepository careerRoleRepository,
                        CareerRoleSkillRepository careerRoleSkillRepository,
                        LearningResourceRepository learningResourceRepository,
                        SkillRepository skillRepository) {
        this.careerRoleRepository = careerRoleRepository;
        this.careerRoleSkillRepository = careerRoleSkillRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<CareerDto> getAllCareers() {
        return careerRoleRepository.findAll().stream()
                .map(this::toCareerDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CareerDto getCareerDto(Long id) {
        CareerRole role = findById(id);
        return toCareerDto(role);
    }

    @Transactional(readOnly = true)
    public List<CareerSkillDto> getCareerSkills(Long careerId) {
        CareerRole careerRole = findById(careerId);
        return careerRoleSkillRepository.findByCareerRoleOrderBySequenceAsc(careerRole).stream()
                .map(this::toCareerSkillDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CareerRole findById(Long id) {
        return careerRoleRepository.findById(id)
                .orElseThrow(() -> new ApiException("Career not found.", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public CareerDto createCareer(CareerRequest request) {
        if (careerRoleRepository.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new ApiException("Career already exists.", HttpStatus.CONFLICT);
        }

        CareerRole careerRole = new CareerRole(request.name(), request.description(), request.targetAudience());
        CareerRole saved = careerRoleRepository.save(careerRole);

        if (request.requiredSkillNames() != null) {
            for (int i = 0; i < request.requiredSkillNames().size(); i++) {
                String skillName = request.requiredSkillNames().get(i).trim();
                if (skillName.isBlank()) {
                    continue;
                }
                Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                        .orElseGet(() -> skillRepository.save(new Skill(skillName, "General")));
                CareerRoleSkill roleSkill = new CareerRoleSkill(saved, skill, 1, i + 1);
                saved.getRequiredSkills().add(roleSkill);
                careerRoleSkillRepository.save(roleSkill);
            }
        }

        return toCareerDto(saved);
    }

    @Transactional
    public CareerDto updateCareer(Long id, CareerRequest request) {
        CareerRole careerRole = findById(id);
        careerRole.setName(request.name());
        careerRole.setDescription(request.description());
        careerRole.setTargetAudience(request.targetAudience());
        CareerRole saved = careerRoleRepository.save(careerRole);

        List<CareerRoleSkill> existingSkills = new ArrayList<>(careerRoleSkillRepository.findByCareerRoleOrderBySequenceAsc(saved));
        for (CareerRoleSkill relationship : existingSkills) {
            careerRoleSkillRepository.delete(relationship);
        }
        saved.getRequiredSkills().clear();

        if (request.requiredSkillNames() != null) {
            for (int i = 0; i < request.requiredSkillNames().size(); i++) {
                String skillName = request.requiredSkillNames().get(i).trim();
                if (skillName.isBlank()) {
                    continue;
                }
                Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                        .orElseGet(() -> skillRepository.save(new Skill(skillName, "General")));
                CareerRoleSkill rel = new CareerRoleSkill(saved, skill, 1, i + 1);
                saved.getRequiredSkills().add(rel);
                careerRoleSkillRepository.save(rel);
            }
        }

        return toCareerDto(saved);
    }

    @Transactional
    public void deleteCareer(Long id) {
        CareerRole careerRole = findById(id);
        careerRoleSkillRepository.deleteAll(careerRoleSkillRepository.findByCareerRoleOrderBySequenceAsc(careerRole));
        learningResourceRepository.findByCareerRoleOrderByIdAsc(careerRole).forEach(learningResourceRepository::delete);
        careerRoleRepository.delete(careerRole);
    }

    private CareerDto toCareerDto(CareerRole careerRole) {
        return new CareerDto(
                careerRole.getId(),
                careerRole.getName(),
                careerRole.getDescription(),
                careerRole.getTargetAudience()
        );
    }

    private CareerSkillDto toCareerSkillDto(CareerRoleSkill skillRelation) {
        return new CareerSkillDto(
                skillRelation.getSkill().getId(),
                skillRelation.getSkill().getName(),
                skillRelation.getSkill().getCategory(),
                skillRelation.getRequiredLevel()
        );
    }
}
