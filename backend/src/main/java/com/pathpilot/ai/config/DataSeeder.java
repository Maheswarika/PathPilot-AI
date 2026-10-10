package com.pathpilot.ai.config;

import com.pathpilot.ai.entity.*;
import com.pathpilot.ai.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Profile("!test")
public class DataSeeder implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final CareerRoleSkillRepository careerRoleSkillRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      SkillRepository skillRepository,
                      CareerRoleRepository careerRoleRepository,
                      CareerRoleSkillRepository careerRoleSkillRepository,
                      LearningResourceRepository learningResourceRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.skillRepository = skillRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.careerRoleSkillRepository = careerRoleSkillRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedSkills();
        seedCareerRoles();
        seedAdmin();
    }

    private void seedRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }

    private void seedSkills() {
        List<String[]> baseSkills = List.of(
                new String[]{"Java", "Programming"},
                new String[]{"Spring Boot", "Backend"},
                new String[]{"REST APIs", "Backend"},
                new String[]{"SQL", "Data"},
                new String[]{"JavaScript", "Frontend"},
                new String[]{"React", "Frontend"},
                new String[]{"HTML", "Frontend"},
                new String[]{"CSS", "Frontend"},
                new String[]{"Python", "Programming"},
                new String[]{"Machine Learning", "AI"},
                new String[]{"Data Analysis", "Data"},
                new String[]{"Excel", "Tools"},
                new String[]{"Problem Solving", "Core"},
                new String[]{"System Design", "Architecture"},
                new String[]{"Testing", "Quality"},
                new String[]{"Git", "Tools"},
                new String[]{"Algorithms", "Programming"},
                new String[]{"Data Structures", "Programming"},
                new String[]{"Cloud Basics", "Infrastructure"},
                new String[]{"Deep Learning", "AI"},
                new String[]{"Statistics", "Data"}
        );

        for (String[] item : baseSkills) {
            String name = item[0];
            String category = item[1];
            if (skillRepository.findByNameIgnoreCase(name).isEmpty()) {
                skillRepository.save(new Skill(name, category));
            }
        }
    }

    private void seedCareerRoles() {
        String[] careerNames = {"Frontend Developer", "Backend Developer", "Full-Stack Developer", "Data Analyst", "Machine Learning Engineer", "AI Engineer", "Java Developer"};
        for (String name : careerNames) {
            if (careerRoleRepository.findByNameIgnoreCase(name).isPresent()) {
                continue;
            }

            CareerRole role = new CareerRole(
                    name,
                    switch (name) {
                        case "Frontend Developer" -> "Build responsive and accessible user interfaces with modern JavaScript frameworks and strong design sensibility.";
                        case "Backend Developer" -> "Design APIs, data flows, and scalable server-side logic that support digital products and business workflows.";
                        case "Full-Stack Developer" -> "Create end-to-end features by combining frontend, backend, and deployment knowledge across the product lifecycle.";
                        case "Data Analyst" -> "Interpret data, design dashboards, and communicate insights that support product and business decisions.";
                        case "Machine Learning Engineer" -> "Build and deploy machine learning systems using data pipelines, model evaluation, and production workflows.";
                        case "AI Engineer" -> "Design AI-powered workflows, integrate models, and create practical solutions with a human-centered approach.";
                        case "Java Developer" -> "Develop robust enterprise applications using Java, Spring Boot, and clean backend architecture patterns.";
                        default -> "Career role for technology students.";
                    },
                    "College students preparing for internships and entry-level tech roles"
            );
            role = careerRoleRepository.save(role);

            if (name.equals("Frontend Developer")) {
                addRequiredSkill(role, "HTML", 1); addRequiredSkill(role, "CSS", 1); addRequiredSkill(role, "JavaScript", 2); addRequiredSkill(role, "React", 2); addRequiredSkill(role, "Git", 1); addRequiredSkill(role, "Problem Solving", 1);
                addResource(role, "MDN HTML Guide", "HTML basics and semantics", "Guide", "https://developer.mozilla.org/en-US/docs/Learn/HTML", "HTML");
                addResource(role, "CSS Tricks", "Modern CSS layout and design patterns", "Article", "https://css-tricks.com/", "CSS");
                addResource(role, "React Docs", "Official React learning materials", "Docs", "https://react.dev/learn", "React");
            } else if (name.equals("Backend Developer")) {
                addRequiredSkill(role, "Java", 2); addRequiredSkill(role, "Spring Boot", 2); addRequiredSkill(role, "REST APIs", 2); addRequiredSkill(role, "SQL", 1); addRequiredSkill(role, "Git", 1); addRequiredSkill(role, "System Design", 1);
                addResource(role, "Spring Guides", "Hands-on Spring ecosystem tutorials", "Docs", "https://spring.io/guides", "Spring Boot");
                addResource(role, "Java Tutorial", "Java fundamentals and basics", "Tutorial", "https://docs.oracle.com/en/java/javase/tutorial/", "Java");
                addResource(role, "PostgreSQL Tutorial", "SQL fundamentals and database concepts", "Tutorial", "https://www.postgresqltutorial.com/", "SQL");
            } else if (name.equals("Full-Stack Developer")) {
                addRequiredSkill(role, "JavaScript", 2); addRequiredSkill(role, "React", 2); addRequiredSkill(role, "REST APIs", 2); addRequiredSkill(role, "Java", 2); addRequiredSkill(role, "Spring Boot", 2); addRequiredSkill(role, "Git", 1);
                addResource(role, "Frontend Mentor", "Practice frontend UI challenges", "Course", "https://www.frontendmentor.io/", "React");
                addResource(role, "Spring Guides", "Backend backend and API development", "Docs", "https://spring.io/guides", "Spring Boot");
            } else if (name.equals("Data Analyst")) {
                addRequiredSkill(role, "SQL", 2); addRequiredSkill(role, "Data Analysis", 2); addRequiredSkill(role, "Statistics", 2); addRequiredSkill(role, "Python", 1); addRequiredSkill(role, "Problem Solving", 1); addRequiredSkill(role, "Excel", 1);
                addResource(role, "Khan Academy Statistics", "Probability and data interpretation", "Course", "https://www.khanacademy.org/math/statistics-probability", "Statistics");
                addResource(role, "SQLBolt", "Learn SQL through practice", "Tutorial", "https://sqlbolt.com/", "SQL");
            } else if (name.equals("Machine Learning Engineer")) {
                addRequiredSkill(role, "Python", 2); addRequiredSkill(role, "Machine Learning", 2); addRequiredSkill(role, "Statistics", 2); addRequiredSkill(role, "Data Analysis", 2); addRequiredSkill(role, "Algorithms", 1); addRequiredSkill(role, "Testing", 1);
                addResource(role, "Scikit-learn", "Machine learning library docs", "Docs", "https://scikit-learn.org/stable/", "Machine Learning");
                addResource(role, "Kaggle Learn", "Intro to ML and data science", "Course", "https://www.kaggle.com/learn", "Machine Learning");
            } else if (name.equals("AI Engineer")) {
                addRequiredSkill(role, "Python", 2); addRequiredSkill(role, "Machine Learning", 2); addRequiredSkill(role, "Deep Learning", 2); addRequiredSkill(role, "Data Analysis", 1); addRequiredSkill(role, "REST APIs", 1); addRequiredSkill(role, "Problem Solving", 1);
                addResource(role, "Google AI Education", "AI fundamentals and applications", "Course", "https://ai.google/education/", "Machine Learning");
                addResource(role, "PyTorch Tutorials", "Deep learning tutorials", "Docs", "https://pytorch.org/tutorials/", "Deep Learning");
            } else if (name.equals("Java Developer")) {
                addRequiredSkill(role, "Java", 2); addRequiredSkill(role, "Spring Boot", 2); addRequiredSkill(role, "REST APIs", 2); addRequiredSkill(role, "SQL", 1); addRequiredSkill(role, "Testing", 1); addRequiredSkill(role, "Git", 1);
                addResource(role, "Spring Quickstart", "Getting started with Spring", "Guide", "https://spring.io/quickstart", "Spring Boot");
                addResource(role, "Java Guides", "Enterprise Java learning path", "Tutorial", "https://www.javaguides.net/", "Java");
            }
        }
    }

    private void addRequiredSkill(CareerRole role, String skillName, int requiredLevel) {
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseThrow(() -> new IllegalArgumentException("Missing skill: " + skillName));
        CareerRoleSkill careerRoleSkill = new CareerRoleSkill(role, skill, requiredLevel, 0);
        role.getRequiredSkills().add(careerRoleSkill);
        careerRoleSkillRepository.save(careerRoleSkill);
    }

    private void addResource(CareerRole role, String title, String description, String type, String url, String skillName) {
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElse(null);
        LearningResource resource = new LearningResource(title, description, type, url, role, skill);
        learningResourceRepository.save(resource);
    }

    private void seedAdmin() {
        String email = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_EMAIL", "admin@pathpilot.local");
        String password = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_PASSWORD", "ChangeMe123!");
        if (email == null || email.isBlank()) {
            return;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseGet(() -> roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN)));
        User admin = new User("PathPilot Admin", email, passwordEncoder.encode(password));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);
    }
}
