package com.ISII.gestion_torneos_tenis.controller;

// src/main/java/com/tuempresa/gestiontorneostenis/controller/InscripcionController.java


import com.ISII.gestion_torneos_tenis.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    // Manejar inscripción a torneo
    @PostMapping("/inscribir")
    public String inscribirTorneo(@RequestParam Long jugadorId, @RequestParam Long torneoId, Model model) {
        ResponseEntity<?> response = inscripcionService.inscribirTorneo(jugadorId, torneoId);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }
        return "redirect:/torneos";
    }

    // Otros métodos como listar inscripciones pueden añadirse aquí
}
