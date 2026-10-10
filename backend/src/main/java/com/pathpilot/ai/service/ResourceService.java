package com.pathpilot.ai.service;

import com.pathpilot.ai.dto.LearningResourceDto;
import com.pathpilot.ai.dto.ResourceRequest;
import com.pathpilot.ai.entity.CareerRole;
import com.pathpilot.ai.entity.LearningResource;
import com.pathpilot.ai.entity.Skill;
import com.pathpilot.ai.exception.ApiException;
import com.pathpilot.ai.repository.CareerRoleRepository;
import com.pathpilot.ai.repository.LearningResourceRepository;
import com.pathpilot.ai.repository.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceService {
    private final LearningResourceRepository learningResourceRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final SkillRepository skillRepository;

    public ResourceService(LearningResourceRepository learningResourceRepository,
                          CareerRoleRepository careerRoleRepository,
                          SkillRepository skillRepository) {
        this.learningResourceRepository = learningResourceRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDto> getAllResources() {
        return learningResourceRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDto> getResourcesForCareer(Long careerId) {
        CareerRole careerRole = careerRoleRepository.findById(careerId)
                .orElseThrow(() -> new ApiException("Career not found.", HttpStatus.NOT_FOUND));
        return learningResourceRepository.findByCareerRoleOrderByIdAsc(careerRole).stream().map(this::toDto).toList();
    }

    @Transactional
    public LearningResourceDto createResource(ResourceRequest request) {
        CareerRole careerRole = careerRoleRepository.findById(request.careerId())
                .orElseThrow(() -> new ApiException("Career not found.", HttpStatus.NOT_FOUND));

        Skill skill = null;
        if (request.skillName() != null && !request.skillName().isBlank()) {
            skill = skillRepository.findByNameIgnoreCase(request.skillName())
                    .orElseGet(() -> skillRepository.save(new Skill(request.skillName(), "General")));
        }

        LearningResource resource = new LearningResource(request.title(), request.description(), request.type(), request.url(), careerRole, skill);
        return toDto(learningResourceRepository.save(resource));
    }

    @Transactional
    public LearningResourceDto updateResource(Long resourceId, ResourceRequest request) {
        LearningResource resource = learningResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ApiException("Resource not found.", HttpStatus.NOT_FOUND));

        Skill skill = null;
        if (request.skillName() != null && !request.skillName().isBlank()) {
            skill = skillRepository.findByNameIgnoreCase(request.skillName())
                    .orElseGet(() -> skillRepository.save(new Skill(request.skillName(), "General")));
        }

        resource.setTitle(request.title());
        resource.setDescription(request.description());
        resource.setType(request.type());
        resource.setUrl(request.url());
        resource.setSkill(skill);
        if (request.careerId() != null) {
            CareerRole careerRole = careerRoleRepository.findById(request.careerId())
                    .orElseThrow(() -> new ApiException("Career not found.", HttpStatus.NOT_FOUND));
            resource.setCareerRole(careerRole);
        }
        return toDto(learningResourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        LearningResource resource = learningResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ApiException("Resource not found.", HttpStatus.NOT_FOUND));
        learningResourceRepository.delete(resource);
    }

    private LearningResourceDto toDto(LearningResource resource) {
        return new LearningResourceDto(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getType(),
                resource.getUrl(),
                resource.getCareerRole() != null ? resource.getCareerRole().getName() : null,
                resource.getSkill() != null ? resource.getSkill().getName() : null
        );
    }
}
