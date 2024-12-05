// src/main/java/com/ISII/gestion_torneos_tenis/service/ResultadoService.java

package com.ISII.gestion_torneos_tenis.service;

import com.ISII.gestion_torneos_tenis.model.Emparejamiento;
import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Resultado;
import com.ISII.gestion_torneos_tenis.repository.ResultadoRepository;
import com.ISII.gestion_torneos_tenis.repository.EmparejamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository resultadoRepository;

    @Autowired
    private EmparejamientoRepository emparejamientoRepository;

    /**
     * Calcular y registrar el ganador del partido basado en los resultados de los sets.
     * Reglas simplificadas: El jugador que gana más sets es el ganador.
     *
     * @param emparejamientoId ID del emparejamiento.
     * @return ResponseEntity con mensaje de éxito o error.
     */
    public ResponseEntity<String> calcularGanadorPartido(Long emparejamientoId) {
        // Obtener el emparejamiento
        Emparejamiento emparejamiento = emparejamientoRepository.findById(emparejamientoId)
                .orElse(null);

        if (emparejamiento == null) {
            return ResponseEntity.badRequest().body("Emparejamiento no encontrado.");
        }

        // Obtener los resultados del partido
        List<Resultado> resultados = resultadoRepository.findByEmparejamiento(emparejamiento);

        if (resultados.isEmpty()) {
            return ResponseEntity.badRequest().body("No hay resultados para este partido.");
        }

        // Contar sets ganados por cada jugador
        int setsGanadosJugador1 = 0;
        int setsGanadosJugador2 = 0;

        for (Resultado resultado : resultados) {
            if (resultado.getGanador() != null) {
                if (resultado.getGanador().equals(emparejamiento.getJugador1())) {
                    setsGanadosJugador1++;
                } else if (resultado.getGanador().equals(emparejamiento.getJugador2())) {
                    setsGanadosJugador2++;
                }
            }
        }

        // Determinar el ganador
        Jugador ganador;
        if (setsGanadosJugador1 > setsGanadosJugador2) {
            ganador = emparejamiento.getJugador1();
        } else if (setsGanadosJugador2 > setsGanadosJugador1) {
            ganador = emparejamiento.getJugador2();
        } else {
            return ResponseEntity.badRequest().body("El partido terminó en empate, se requiere desempate.");
        }

        // Actualizar los resultados con el ganador final si es necesario
        // Aquí podrías implementar lógica adicional si deseas almacenar el ganador final

        return ResponseEntity.ok("El ganador del partido es: " + ganador.getNombreUsuario());
    }
}
