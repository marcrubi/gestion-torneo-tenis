package com.ISII.gestion_torneos_tenis.repository;

import com.ISII.gestion_torneos_tenis.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    List<Torneo> findByEstadoTorneo(String estadoTorneo);
    List<Torneo> findAll();
}
