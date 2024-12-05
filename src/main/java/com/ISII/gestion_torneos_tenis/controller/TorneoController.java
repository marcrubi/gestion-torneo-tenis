// src/main/java/com/ISII/gestion_torneos_tenis/controller/TorneoController.java

package com.ISII.gestion_torneos_tenis.controller;

import org.springframework.stereotype.Controller;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import java.util.List;
import com.ISII.gestion_torneos_tenis.service.TorneoService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/torneos/disponibles")
    public String visualizarTorneosDisponibles(Model model) {
        // Obtener la lista de torneos disponibles
        List<Torneo> listaDeTorneos = torneoService.obtenerTorneosDisponibles();
        // Añadir la lista al modelo
        model.addAttribute("torneos", listaDeTorneos);
        return "torneos_disponibles"; // Asegúrate de que torneos_disponibles.html existe en templates
    }
    @GetMapping("/torneos/seleccionar-jugadores")
    public String seleccionarJugadores(Model model) {
        // Lógica para seleccionar jugadores para torneos
        // model.addAttribute("jugadores", listaDeJugadoresDisponibles);
        return "seleccionar_jugadores"; // Crea seleccionar_jugadores.html en templates
    }


}
