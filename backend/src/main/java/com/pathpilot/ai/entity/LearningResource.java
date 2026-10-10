package com.pathpilot.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_resources")
public class LearningResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, length = 1000)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_role_id")
    private CareerRole careerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    public LearningResource() {
    }

    public LearningResource(String title, String description, String type, String url, CareerRole careerRole, Skill skill) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
        this.careerRole = careerRole;
        this.skill = skill;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
