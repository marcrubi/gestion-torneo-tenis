// src/main/java/com/ISII/gestion_torneos_tenis/repository/RoleRepository.java

package com.ISII.gestion_torneos_tenis.repository;

import com.ISII.gestion_torneos_tenis.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
