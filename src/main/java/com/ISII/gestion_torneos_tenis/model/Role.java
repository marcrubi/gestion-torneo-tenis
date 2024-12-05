// src/main/java/com/ISII/gestion_torneos_tenis/model/Role.java

package com.ISII.gestion_torneos_tenis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Constructores

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    // Getters y Setters

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
