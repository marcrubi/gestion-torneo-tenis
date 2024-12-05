// src/main/java/com/ISII/gestion_torneos_tenis/controller/AdminController.java

package com.ISII.gestion_torneos_tenis.controller;

import com.ISII.gestion_torneos_tenis.model.JugadorRanking;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.service.RankingService;
import com.ISII.gestion_torneos_tenis.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private RankingService rankingService;

    /**
     * Mostrar la página para seleccionar jugadores para un torneo.
     */
    @GetMapping("/seleccionar-jugadores/{torneoId}")
    public String mostrarSeleccionJugadores(@PathVariable Long torneoId, Model model) {
        Torneo torneo = torneoService.getTorneoById(torneoId);
        List<JugadorRanking> rankingList = rankingService.calcularRankingGlobal();

        // Filtrar jugadores inscritos en el torneo
        List<JugadorRanking> jugadoresInscritos = rankingList.stream()
                .filter(jr -> torneoService.estaJugadorInscritoEnTorneo(jr.getJugador().getId(), torneoId))
                .collect(Collectors.toList());

        model.addAttribute("torneo", torneo);
        model.addAttribute("jugadores", jugadoresInscritos);
        return "admin/seleccionar_jugadores"; // Crea seleccionar_jugadores.html en templates/admin
    }

    @GetMapping("/torneos")
    public String listarTorneos(Model model) {
        List<Torneo> torneos = torneoService.obtenerTodosLosTorneos();
        model.addAttribute("torneos", torneos);
        return "admin/torneos_lista";
    }


    /**
     * Manejar la selección de jugadores para el torneo.
     */
    @PostMapping("/seleccionar-jugadores/{torneoId}")
    public String seleccionarJugadores(@PathVariable Long torneoId,
                                       @RequestParam List<Long> jugadorIds,
                                       Model model) {
        ResponseEntity<String> response = torneoService.seleccionarJugadoresParaTorneo(torneoId, jugadorIds);

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }

        Torneo torneo = torneoService.getTorneoById(torneoId);
        List<JugadorRanking> rankingList = rankingService.calcularRankingGlobal();

        List<JugadorRanking> jugadoresInscritos = rankingList.stream()
                .filter(jr -> torneoService.estaJugadorInscritoEnTorneo(jr.getJugador().getId(), torneoId))
                .collect(Collectors.toList());

        model.addAttribute("torneo", torneo);
        model.addAttribute("jugadores", jugadoresInscritos);

        return "admin/seleccionar_jugadores";
    }

    /**
     * Mostrar la página para generar emparejamientos para un torneo.
     */
    @GetMapping("/generar-emparejamientos/{torneoId}")
    public String generarEmparejamientos(@PathVariable Long torneoId, Model model) {
        ResponseEntity<String> response = torneoService.generarEmparejamientos(torneoId);

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }

        Torneo torneo = torneoService.getTorneoById(torneoId);
        model.addAttribute("torneo", torneo);
        model.addAttribute("emparejamientos", torneo.getEmparejamientos());

        return "admin/generar_emparejamientos"; // Crea generar_emparejamientos.html en templates/admin
    }
}
