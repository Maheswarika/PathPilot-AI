package com.pathpilot.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roadmap_steps")
public class RoadmapStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_resource_id")
    private LearningResource learningResource;

    public RoadmapStep() {
    }

    public RoadmapStep(Roadmap roadmap, Skill skill, Integer sequence, String title, String description, LearningResource learningResource) {
        this.roadmap = roadmap;
        this.skill = skill;
        this.sequence = sequence;
        this.title = title;
        this.description = description;
        this.learningResource = learningResource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Roadmap getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(Roadmap roadmap) {
        this.roadmap = roadmap;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LearningResource getLearningResource() {
        return learningResource;
    }

    public void setLearningResource(LearningResource learningResource) {
        this.learningResource = learningResource;
    }
}
