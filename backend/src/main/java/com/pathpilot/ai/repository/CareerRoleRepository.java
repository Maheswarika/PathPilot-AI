package com.pathpilot.ai.repository;

import com.pathpilot.ai.entity.CareerRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareerRoleRepository extends JpaRepository<CareerRole, Long> {
    Optional<CareerRole> findByNameIgnoreCase(String name);
}
