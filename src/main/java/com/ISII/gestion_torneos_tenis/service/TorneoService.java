// src/main/java/com/ISII/gestion_torneos_tenis/service/TorneoService.java

package com.ISII.gestion_torneos_tenis.service;

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Inscripcion;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.model.Emparejamiento;
import com.ISII.gestion_torneos_tenis.model.JugadorRanking;
import com.ISII.gestion_torneos_tenis.repository.TorneoRepository;
import com.ISII.gestion_torneos_tenis.repository.InscripcionRepository;
import com.ISII.gestion_torneos_tenis.repository.JugadorRepository;
import com.ISII.gestion_torneos_tenis.repository.EmparejamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private EmparejamientoRepository emparejamientoRepository;

    @Autowired
    private RankingService rankingService;

    /**
     * Obtener todos los torneos que están disponibles para inscripción.
     */

    /**
     * Obtener un torneo por su ID.
     */
    public Torneo getTorneoById(Long torneoId) {
        return torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
    }


    public List<Torneo> obtenerTorneosDisponibles() {
        return torneoRepository.findByEstadoTorneo("Pendiente");
    }



    /**
     * Seleccionar hasta 16 jugadores para un torneo basado en su ranking.
     */
    public ResponseEntity<String> seleccionarJugadoresParaTorneo(Long torneoId, List<Long> jugadorIds) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isEmpty()) {
            return new ResponseEntity<>("Torneo no encontrado.", HttpStatus.NOT_FOUND);
        }

        Torneo torneo = torneoOpt.get();

        // Verificar si el torneo está disponible para la selección de jugadores
        if (!torneo.getEstadoTorneo().equalsIgnoreCase("Disponible")) {
            return new ResponseEntity<>("El torneo no está disponible para la selección de jugadores.", HttpStatus.BAD_REQUEST);
        }

        // Verificar que no se exceda el número máximo de jugadores
        List<Inscripcion> inscripcionesSeleccionadas = inscripcionRepository.findByTorneo(torneo).stream()
                .filter(ins -> ins.getEstadoInscripcion().equalsIgnoreCase("Seleccionado"))
                .collect(Collectors.toList());

        int jugadoresYaSeleccionados = inscripcionesSeleccionadas.size();
        int jugadoresADesplegar = Math.min(jugadorIds.size(), torneo.getNumeroMaxJugadores() - jugadoresYaSeleccionados);

        if (jugadoresADesplegar <= 0) {
            return new ResponseEntity<>("El torneo ya ha alcanzado el número máximo de jugadores.", HttpStatus.BAD_REQUEST);
        }

        // Calcular el ranking global
        List<JugadorRanking> rankingList = rankingService.calcularRankingGlobal();

        // Filtrar los jugadores que están en la lista de IDs proporcionados y están inscritos en el torneo
        List<JugadorRanking> jugadoresFiltrados = rankingList.stream()
                .filter(jr -> jugadorIds.contains(jr.getJugador().getId()) && estaJugadorInscritoEnTorneo(jr.getJugador().getId(), torneoId))
                .collect(Collectors.toList());

        // Ordenar los jugadores filtrados por ranking (partidos ganados, sets ganados, juegos ganados)
        Collections.sort(jugadoresFiltrados);

        // Seleccionar los primeros N jugadores según el límite
        List<JugadorRanking> jugadoresSeleccionados = jugadoresFiltrados.stream()
                .limit(jugadoresADesplegar)
                .collect(Collectors.toList());

        // Actualizar las inscripciones
        for (JugadorRanking jr : jugadoresSeleccionados) {
            Jugador jugador = jr.getJugador();
            Optional<Inscripcion> inscripcionOpt = inscripcionRepository.findByJugadorAndTorneo(jugador, torneo);
            if (inscripcionOpt.isPresent()) {
                Inscripcion inscripcion = inscripcionOpt.get();
                inscripcion.setEstadoInscripcion("Seleccionado");
                inscripcionRepository.save(inscripcion);
            }
        }

        // Verificar si se han seleccionado el número máximo de jugadores
        inscripcionesSeleccionadas = inscripcionRepository.findByTorneo(torneo).stream()
                .filter(ins -> ins.getEstadoInscripcion().equalsIgnoreCase("Seleccionado"))
                .collect(Collectors.toList());

        if (inscripcionesSeleccionadas.size() >= torneo.getNumeroMaxJugadores()) {
            torneo.setEstadoTorneo("Jugadores Seleccionados");
            torneoRepository.save(torneo);
        }

        return new ResponseEntity<>("Jugadores seleccionados exitosamente para el torneo.", HttpStatus.OK);
    }

    /**
     * Generar emparejamientos automáticos para un torneo.
     */
    public ResponseEntity<String> generarEmparejamientos(Long torneoId) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isEmpty()) {
            return new ResponseEntity<>("Torneo no encontrado.", HttpStatus.NOT_FOUND);
        }

        Torneo torneo = torneoOpt.get();

        // Verificar que el torneo ha seleccionado jugadores
        if (!torneo.getEstadoTorneo().equalsIgnoreCase("Jugadores Seleccionados")) {
            return new ResponseEntity<>("No se han seleccionado los jugadores para este torneo.", HttpStatus.BAD_REQUEST);
        }

        // Obtener las inscripciones seleccionadas
        List<Inscripcion> inscripcionesSeleccionadas = inscripcionRepository.findByTorneo(torneo).stream()
                .filter(ins -> ins.getEstadoInscripcion().equalsIgnoreCase("Seleccionado"))
                .collect(Collectors.toList());

        List<Jugador> jugadores = inscripcionesSeleccionadas.stream()
                .map(Inscripcion::getJugador)
                .collect(Collectors.toList());

        if (jugadores.size() < 2) {
            return new ResponseEntity<>("No hay suficientes jugadores para generar emparejamientos.", HttpStatus.BAD_REQUEST);
        }

        // Mezclar la lista de jugadores para emparejarlos aleatoriamente
        Collections.shuffle(jugadores);

        List<Emparejamiento> emparejamientos = new ArrayList<>();

        int ronda = 1; // Primera ronda

        for (int i = 0; i < jugadores.size(); i += 2) {
            Jugador jugador1 = jugadores.get(i);
            Jugador jugador2 = (i + 1 < jugadores.size()) ? jugadores.get(i + 1) : null;

            Emparejamiento emparejamiento = new Emparejamiento(torneo, ronda, jugador1, jugador2, null);
            emparejamientos.add(emparejamiento);
            emparejamientoRepository.save(emparejamiento);
        }

        torneo.setEstadoTorneo("Emparejamientos Generados");
        torneoRepository.save(torneo);

        return new ResponseEntity<>("Emparejamientos generados exitosamente para el torneo.", HttpStatus.OK);
    }

    /**
     * Método privado para generar emparejamientos de la primera ronda.
     */
    public List<Emparejamiento> generarEmparejamientosPrimeraRonda(Torneo torneo, List<Emparejamiento> emparejamientos) {
        List<Emparejamiento> nuevosEmparejamientos = new ArrayList<>();

        // Obtener las inscripciones seleccionadas
        List<Inscripcion> inscripcionesSeleccionadas = inscripcionRepository.findByTorneo(torneo).stream()
                .filter(ins -> ins.getEstadoInscripcion().equalsIgnoreCase("Seleccionado"))
                .collect(Collectors.toList());

        List<Jugador> jugadores = inscripcionesSeleccionadas.stream()
                .map(Inscripcion::getJugador)
                .collect(Collectors.toList());

        // Calcular el ranking y ordenar los jugadores según él
        List<JugadorRanking> rankingList = rankingService.calcularRankingGlobal();

        // Filtrar solo los jugadores del torneo
        List<JugadorRanking> jugadoresRanking = rankingList.stream()
                .filter(jr -> jugadores.contains(jr.getJugador()))
                .collect(Collectors.toList());

        // Ordenar los jugadores según el ranking
        Collections.sort(jugadoresRanking);

        // Extraer los jugadores ordenados
        List<Jugador> jugadoresOrdenados = jugadoresRanking.stream()
                .map(JugadorRanking::getJugador)
                .collect(Collectors.toList());

        // Mezclar la lista de jugadores para emparejarlos aleatoriamente
        Collections.shuffle(jugadoresOrdenados);

        int ronda = 1;

        for (int i = 0; i < jugadoresOrdenados.size(); i += 2) {
            Jugador jugador1 = jugadoresOrdenados.get(i);
            Jugador jugador2 = (i + 1 < jugadoresOrdenados.size()) ? jugadoresOrdenados.get(i + 1) : null;

            Emparejamiento emparejamiento = new Emparejamiento(torneo, ronda, jugador1, jugador2, null);
            nuevosEmparejamientos.add(emparejamiento);
            emparejamientoRepository.save(emparejamiento);
        }

        return nuevosEmparejamientos;
    }

    /**
     * Verificar si un jugador está inscrito en un torneo.
     */
    public boolean estaJugadorInscritoEnTorneo(Long jugadorId, Long torneoId) {
        Optional<Inscripcion> inscripcion = inscripcionRepository.findByJugadorIdAndTorneoId(jugadorId, torneoId);
        return inscripcion.isPresent();
    }

    /**
     * Obtener el ID del torneo asociado a un emparejamiento.
     */
    public Long obtenerTorneoPorEmparejamiento(Long emparejamientoId) {
        Optional<Emparejamiento> emparejamientoOpt = emparejamientoRepository.findById(emparejamientoId);
        if (emparejamientoOpt.isEmpty()) {
            throw new RuntimeException("Emparejamiento no encontrado con ID: " + emparejamientoId);
        }
        return emparejamientoOpt.get().getTorneo().getId();
    }
    public List<Torneo> obtenerTodosLosTorneos() {
        return torneoRepository.findAll();
    }



}
