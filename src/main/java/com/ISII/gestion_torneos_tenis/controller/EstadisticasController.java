// src/main/java/com/ISII/gestion_torneos_tenis/controller/EstadisticasController.java

package com.ISII.gestion_torneos_tenis.controller;

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.service.RankingService;
import com.ISII.gestion_torneos_tenis.service.JugadorService;
import com.ISII.gestion_torneos_tenis.model.JugadorRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstadisticasController {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private JugadorService jugadorService;

    /**
     * Mostrar estadísticas personales del jugador autenticado.
     */
    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Authentication authentication, Model model) {
        String nombreUsuario = authentication.getName();
        Jugador jugador = jugadorService.obtenerJugadorPorNombreUsuario(nombreUsuario);

        // Obtener estadísticas del jugador
        JugadorRanking jugadorRanking = rankingService.calcularRankingGlobal().stream()
                .filter(jr -> jr.getJugador().getId().equals(jugador.getId()))
                .findFirst()
                .orElse(null);

        if (jugadorRanking != null) {
            model.addAttribute("jugador", jugador);
            model.addAttribute("partidosGanados", jugadorRanking.getPartidosGanados());
            model.addAttribute("setsGanados", jugadorRanking.getSetsGanados());
            model.addAttribute("setsPerdidos", jugadorRanking.getSetsPerdidos());
            model.addAttribute("juegosGanados", jugadorRanking.getJuegosGanados());
            model.addAttribute("juegosPerdidos", jugadorRanking.getJuegosPerdidos());
            model.addAttribute("posicionRanking", jugadorRanking.getPosicion());
        } else {
            model.addAttribute("errorMessage", "No se encontraron estadísticas para este jugador.");
        }

        return "estadisticas"; // Crea estadisticas.html en templates
    }
}
