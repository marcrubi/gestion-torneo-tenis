// src/main/java/com/ISII/gestion_torneos_tenis/controller/HomeController.java

package com.ISII.gestion_torneos_tenis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "menu";
    }
}
