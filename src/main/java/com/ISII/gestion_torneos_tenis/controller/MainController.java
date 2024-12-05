// src/main/java/com/ISII/gestion_torneos_tenis/controller/MainController.java

package com.ISII.gestion_torneos_tenis.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/main")
public class MainController {

    @GetMapping
    public String mostrarMenuPrincipal(Model model) {
        return "menu"; // Renderiza el archivo src/main/resources/templates/menu.html
    }
}
