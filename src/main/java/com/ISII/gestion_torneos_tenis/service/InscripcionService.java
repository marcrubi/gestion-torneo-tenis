package com.ISII.gestion_torneos_tenis.service;

// src/main/java/com/tuempresa/gestiontorneostenis/service/InscripcionService.java

import com.ISII.gestion_torneos_tenis.model.Inscripcion;
import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.repository.InscripcionRepository;
import com.ISII.gestion_torneos_tenis.repository.JugadorRepository;
import com.ISII.gestion_torneos_tenis.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    public ResponseEntity<?> inscribirTorneo(Long jugadorId, Long torneoId) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(jugadorId);
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);

        if (jugadorOpt.isEmpty() || torneoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Jugador o Torneo no encontrado.");
        }

        Jugador jugador = jugadorOpt.get();
        Torneo torneo = torneoOpt.get();

        // Verificar si ya está inscrito
        Optional<Inscripcion> existente = inscripcionRepository.findByJugadorAndTorneo(jugador, torneo);
        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("El jugador ya está inscrito en este torneo.");
        }

        // Verificar si hay espacio disponible
        List<Inscripcion> inscripciones = inscripcionRepository.findByTorneo(torneo);
        int inscritos = inscripciones.size();
        if (inscritos >= torneo.getNumeroMaxJugadores()) {
            return ResponseEntity.badRequest().body("El torneo ha alcanzado el número máximo de jugadores.");
        }

        // Crear inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setJugador(jugador);
        inscripcion.setTorneo(torneo);
        inscripcion.setEstadoInscripcion("Inscrito");
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        inscripcionRepository.save(inscripcion);

        return ResponseEntity.ok("Inscripción exitosa al torneo.");
    }

    public List<Inscripcion> obtenerInscripcionesPorTorneo(Long torneoId) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isEmpty()) {
            return List.of();
        }
        return inscripcionRepository.findByTorneo(torneoOpt.get());
    }
}
