// src/main/java/com/ISII/gestion_torneos_tenis/model/Resultado.java

package com.ISII.gestion_torneos_tenis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emparejamiento_id")
    private Emparejamiento emparejamiento;

    @NotNull
    private int numeroSet;

    @NotNull
    private int juegosGanadosJugador1;

    @NotNull
    private int juegosGanadosJugador2;

    @ManyToOne
    @JoinColumn(name = "ganador_id")
    private Jugador ganador;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public Resultado() {
    }

    public Resultado(Emparejamiento emparejamiento, int numeroSet, int juegosGanadosJugador1, int juegosGanadosJugador2, Jugador ganador) {
        this.emparejamiento = emparejamiento;
        this.numeroSet = numeroSet;
        this.juegosGanadosJugador1 = juegosGanadosJugador1;
        this.juegosGanadosJugador2 = juegosGanadosJugador2;
        this.ganador = ganador;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public Emparejamiento getEmparejamiento() {
        return emparejamiento;
    }

    public void setEmparejamiento(Emparejamiento emparejamiento) {
        this.emparejamiento = emparejamiento;
    }

    public int getNumeroSet() {
        return numeroSet;
    }

    public void setNumeroSet(int numeroSet) {
        this.numeroSet = numeroSet;
    }

    public int getJuegosGanadosJugador1() {
        return juegosGanadosJugador1;
    }

    public void setJuegosGanadosJugador1(int juegosGanadosJugador1) {
        this.juegosGanadosJugador1 = juegosGanadosJugador1;
    }

    public int getJuegosGanadosJugador2() {
        return juegosGanadosJugador2;
    }

    public void setJuegosGanadosJugador2(int juegosGanadosJugador2) {
        this.juegosGanadosJugador2 = juegosGanadosJugador2;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public void setGanador(Jugador ganador) {
        this.ganador = ganador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
