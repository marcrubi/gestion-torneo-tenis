// src/main/java/com/ISII/gestion_torneos_tenis/service/RankingService.java

package com.ISII.gestion_torneos_tenis.service;

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Resultado;
import com.ISII.gestion_torneos_tenis.model.JugadorRanking;
import com.ISII.gestion_torneos_tenis.repository.JugadorRepository;
import com.ISII.gestion_torneos_tenis.repository.ResultadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private ResultadoRepository resultadoRepository;

    /**
     * Calcula el ranking de todos los jugadores basado en los resultados de los partidos.
     * Mayor cantidad de partidos ganados, sets ganados, y juegos ganados aumentan el ranking.
     *
     * @return Lista de jugadores ordenados por ranking descendente.
     */
    public List<JugadorRanking> calcularRankingGlobal() {
        List<Jugador> jugadores = jugadorRepository.findAll();

        // Map para almacenar estadísticas por jugador
        Map<Long, JugadorRanking> rankingMap = new HashMap<>();

        for (Jugador jugador : jugadores) {
            rankingMap.put(jugador.getId(), new JugadorRanking(jugador));
        }

        // Obtener todos los resultados
        List<Resultado> resultados = resultadoRepository.findAll();

        for (Resultado resultado : resultados) {
            Jugador ganador = resultado.getGanador();
            Jugador jugador1 = resultado.getEmparejamiento().getJugador1();
            Jugador jugador2 = resultado.getEmparejamiento().getJugador2();

            if (ganador != null) {
                JugadorRanking jrGanador = rankingMap.get(ganador.getId());
                jrGanador.incrementPartidosGanados();
                jrGanador.incrementSetsGanados(resultado.getNumeroSet());
                jrGanador.incrementJuegosGanados(resultado.getJuegosGanadosJugador1() + resultado.getJuegosGanadosJugador2());

                Jugador perdedor = ganador.equals(jugador1) ? jugador2 : jugador1;
                JugadorRanking jrPerdedor = rankingMap.get(perdedor.getId());
                jrPerdedor.incrementSetsPerdidos(resultado.getNumeroSet());
                jrPerdedor.incrementJuegosPerdidos(resultado.getJuegosGanadosJugador1() + resultado.getJuegosGanadosJugador2());
            }
        }

        // Convertir el mapa a una lista y ordenar
        List<JugadorRanking> rankingList = new ArrayList<>(rankingMap.values());

        Collections.sort(rankingList);

        // Asignar la posición en el ranking
        int posicion = 1;
        for (JugadorRanking jr : rankingList) {
            jr.setPosicion(posicion++);
        }

        return rankingList;
    }
    public JugadorRanking obtenerEstadisticasJugador(Jugador jugador) {
        // Mapear el jugador a su ranking
        Map<Long, JugadorRanking> rankingMap = calcularRankingGlobalMap();

        JugadorRanking jugadorRanking = rankingMap.get(jugador.getId());
        if (jugadorRanking == null) {
            jugadorRanking = new JugadorRanking(jugador);
        }
        return jugadorRanking;
    }

    private Map<Long, JugadorRanking> calcularRankingGlobalMap() {
        // Método similar a calcularRankingGlobal pero devuelve un Map
        List<Jugador> jugadores = jugadorRepository.findAll();
        Map<Long, JugadorRanking> rankingMap = new HashMap<>();

        for (Jugador jugador : jugadores) {
            rankingMap.put(jugador.getId(), new JugadorRanking(jugador));
        }

        List<Resultado> resultados = resultadoRepository.findAll();

        for (Resultado resultado : resultados) {
            // Lógica para actualizar las estadísticas
        }

        return rankingMap;
    }

}
