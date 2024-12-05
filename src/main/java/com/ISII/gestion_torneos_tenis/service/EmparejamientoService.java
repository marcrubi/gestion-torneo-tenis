package com.ISII.gestion_torneos_tenis.service;

// src/main/java/com/tuempresa/gestiontorneostenis/service/EmparejamientoService.java

import com.ISII.gestion_torneos_tenis.model.Emparejamiento;
import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.model.Inscripcion;
import com.ISII.gestion_torneos_tenis.repository.EmparejamientoRepository;
import com.ISII.gestion_torneos_tenis.repository.InscripcionRepository;
import com.ISII.gestion_torneos_tenis.repository.JugadorRepository;
import com.ISII.gestion_torneos_tenis.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmparejamientoService {

    @Autowired
    private EmparejamientoRepository emparejamientoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    public ResponseEntity<?> generarEmparejamientos(Long torneoId, int ronda) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Torneo no encontrado.");
        }

        Torneo torneo = torneoOpt.get();

        // Obtener jugadores inscritos en el torneo
        List<Inscripcion> inscripciones = inscripcionRepository.findByTorneo(torneo);
        List<Jugador> jugadores = new ArrayList<>();
        for (Inscripcion ins : inscripciones) {
            jugadores.add(ins.getJugador());
        }

        // Verificar si ya existen emparejamientos para esta ronda
        List<Emparejamiento> existentes = emparejamientoRepository.findByTorneoAndNumeroRonda(torneo, ronda);
        if (!existentes.isEmpty()) {
            return ResponseEntity.badRequest().body("Ya se han generado emparejamientos para esta ronda.");
        }

        // Mezclar la lista de jugadores aleatoriamente
        Collections.shuffle(jugadores);

        // Crear emparejamientos
        List<Emparejamiento> emparejamientos = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i += 2) {
            if (i + 1 < jugadores.size()) {
                Emparejamiento emp = new Emparejamiento();
                emp.setTorneo(torneo);
                emp.setNumeroRonda(ronda);
                emp.setJugador1(jugadores.get(i));
                emp.setJugador2(jugadores.get(i + 1));
                emp.setFechaHoraPartido(LocalDateTime.now().plusDays(1)); // Programar para mañana
                emparejamientos.add(emp);
            } else {
                // Si hay un número impar de jugadores, asignarlo automáticamente a la siguiente ronda o darle un pase libre
                // Aquí asignaremos un pase libre (Jugador sin oponente)
                Emparejamiento emp = new Emparejamiento();
                emp.setTorneo(torneo);
                emp.setNumeroRonda(ronda);
                emp.setJugador1(jugadores.get(i));
                emp.setJugador2(null); // No tiene oponente
                emp.setFechaHoraPartido(LocalDateTime.now().plusDays(1));
                emparejamientos.add(emp);
            }
        }

        emparejamientoRepository.saveAll(emparejamientos);

        return ResponseEntity.ok("Emparejamientos generados exitosamente.");
    }

    public List<Emparejamiento> obtenerEmparejamientosPorTorneoYRonda(Long torneoId, int ronda) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isEmpty()) {
            return List.of();
        }

        Torneo torneo = torneoOpt.get();
        List<Emparejamiento> emparejamientos = emparejamientoRepository.findByTorneoAndNumeroRonda(torneo, ronda);
        return emparejamientos;
    }
}
