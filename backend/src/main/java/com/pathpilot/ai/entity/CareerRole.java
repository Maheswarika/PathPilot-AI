package com.pathpilot.ai.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "career_roles", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class CareerRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String targetAudience;

    @OneToMany(mappedBy = "careerRole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerRoleSkill> requiredSkills = new ArrayList<>();

    @OneToMany(mappedBy = "careerRole")
    private List<LearningResource> resources = new ArrayList<>();

    public CareerRole() {
    }

    public CareerRole(String name, String description, String targetAudience) {
        this.name = name;
        this.description = description;
        this.targetAudience = targetAudience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public List<CareerRoleSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<CareerRoleSkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<LearningResource> getResources() {
        return resources;
    }

    public void setResources(List<LearningResource> resources) {
        this.resources = resources;
    }
}
