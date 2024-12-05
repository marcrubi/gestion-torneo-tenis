// src/main/java/com/ISII/gestion_torneos_tenis/repository/InscripcionRepository.java

package com.ISII.gestion_torneos_tenis.repository;

import com.ISII.gestion_torneos_tenis.model.Inscripcion;
import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    Optional<Inscripcion> findByJugadorAndTorneo(Jugador jugador, Torneo torneo);
    List<Inscripcion> findByTorneo(Torneo torneo);
    List<Inscripcion> findByJugador(Jugador jugador);

    // Métodos personalizados
    Optional<Inscripcion> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
}
