package com.pathpilot.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "career_role_skills")
public class CareerRoleSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_role_id", nullable = false)
    private CareerRole careerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Integer requiredLevel = 1;

    @Column(nullable = false)
    private Integer sequence = 0;

    public CareerRoleSkill() {
    }

    public CareerRoleSkill(CareerRole careerRole, Skill skill, Integer requiredLevel, Integer sequence) {
        this.careerRole = careerRole;
        this.skill = skill;
        this.requiredLevel = requiredLevel;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerRole getCareerRole() {
        return careerRole;
    }

    public void setCareerRole(CareerRole careerRole) {
        this.careerRole = careerRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(Integer requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
