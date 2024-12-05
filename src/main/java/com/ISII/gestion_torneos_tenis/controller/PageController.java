package com.ISII.gestion_torneos_tenis.controller;

// src/main/java/com/tuempresa/gestiontorneostenis/controller/PageController.java

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@Controller
public class PageController {

    @Autowired
    private TorneoRepository torneoRepository;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("jugador", new Jugador());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/torneos")
    public String showTorneosPage(Model model) {
        List<Torneo> torneos = torneoRepository.findByEstadoTorneo("Activo"); // Asumiendo "Activo"
        model.addAttribute("torneos", torneos);
        return "torneos";
    }
}

