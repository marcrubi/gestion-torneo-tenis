// src/main/java/com/ISII/gestion_torneos_tenis/controller/EmparejamientoController.java

package com.ISII.gestion_torneos_tenis.controller;

import com.ISII.gestion_torneos_tenis.service.TorneoService;
import com.ISII.gestion_torneos_tenis.service.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/emparejamientos")
public class EmparejamientoController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private ResultadoService resultadoService;

    /**
     * Manejar generación de emparejamientos.
     */
    @PostMapping("/generar")
    public String generarEmparejamientos(@RequestParam Long torneoId, Model model) {
        ResponseEntity<String> response = torneoService.generarEmparejamientos(torneoId);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }
        return "redirect:/admin/generar-emparejamientos/" + torneoId;
    }

    @PostMapping("/calcular-ganador")
    public String calcularGanador(@RequestParam Long emparejamientoId, Model model) {
        ResponseEntity<String> response = resultadoService.calcularGanadorPartido(emparejamientoId);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }
        // Redirigir a la página de emparejamientos generados
        // Asumiendo que conoces el torneoId relacionado
        Long torneoId = torneoService.obtenerTorneoPorEmparejamiento(emparejamientoId);
        return "redirect:/admin/generar-emparejamientos/" + torneoId;
    }

    // Otros métodos como listar emparejamientos pueden añadirse aquí
}
