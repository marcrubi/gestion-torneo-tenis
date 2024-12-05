// src/main/java/com/ISII/gestion_torneos_tenis/model/VerificationToken.java

package com.ISII.gestion_torneos_tenis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "jugador_id")
    private Jugador jugador;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Constructores

    public VerificationToken() {
    }

    public VerificationToken(String token, Jugador jugador, LocalDateTime expiryDate) {
        this.token = token;
        this.jugador = jugador;
        this.expiryDate = expiryDate;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
