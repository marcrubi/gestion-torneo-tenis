package com.ISII.gestion_torneos_tenis.repository;

import com.ISII.gestion_torneos_tenis.model.Emparejamiento;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmparejamientoRepository extends JpaRepository<Emparejamiento, Long> {
    List<Emparejamiento> findByTorneoAndNumeroRonda(Torneo torneo, int numeroRonda);
}
