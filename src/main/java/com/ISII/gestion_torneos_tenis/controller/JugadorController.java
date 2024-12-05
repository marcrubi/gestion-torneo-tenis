// src/main/java/com/ISII/gestion_torneos_tenis/controller/JugadorController.java

package com.ISII.gestion_torneos_tenis.controller;

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.service.JugadorService;
import com.ISII.gestion_torneos_tenis.model.Torneo;
import com.ISII.gestion_torneos_tenis.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    /**
     * Muestra la página de registro de jugadores.
     */
    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("jugador", new Jugador());
        return "register";
    }

    /**
     * Maneja el registro de un nuevo jugador.
     */
    @PostMapping("/register")
    public String registrarJugador(@Valid @ModelAttribute Jugador jugador, org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // Retorna a la vista de registro con mensajes de error
        }

        // Intentar registrar el jugador a través del servicio
        ResponseEntity<String> response = jugadorService.registerJugador(jugador);

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
            return "login"; // Redirigir al login después del registro exitoso
        } else {
            // Añadir error global (no asociado a un campo específico)
            model.addAttribute("errorMessage", response.getBody());
            return "register";
        }
    }

    /**
     * Maneja la verificación de cuenta del jugador.
     */
    @GetMapping("/verificar")
    public String verificarCuenta(@RequestParam String token, Model model) {
        ResponseEntity<String> response = jugadorService.verificarCuenta(token);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }
        return "login";
    }

    /**
     * Muestra la página de login.
     */
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, Model model) {
        if (error != null) {
            if (error.equals("disabled")) {
                model.addAttribute("errorMessage", "Tu cuenta no está verificada. Por favor, verifica tu correo electrónico.");
            } else {
                model.addAttribute("errorMessage", "Nombre de usuario o contraseña incorrectos.");
            }
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Has cerrado sesión correctamente.");
        }
        return "login";
    }

    /**
     * Muestra la página de recuperación de contraseña.
     */
    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperarContrasena(Model model) {
        return "recuperar_contraseña"; // Asegúrate de que existe recuperar_contraseña.html en templates
    }

    /**
     * Maneja la solicitud de recuperación de contraseña.
     */
    @PostMapping("/recuperar")
    public String recuperarContrasena(@RequestParam String correoElectronico, Model model) {
        ResponseEntity<String> response = jugadorService.recuperarContrasena(correoElectronico);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }
        return "recuperar_contraseña";
    }


    /**
     * Muestra la página de perfil del jugador.
     */
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Authentication authentication) {
        String nombreUsuario = authentication.getName();
        Jugador jugador = jugadorService.obtenerJugadorPorNombreUsuario(nombreUsuario);
        model.addAttribute("jugador", jugador);
        return "perfil"; // Asegúrate de que existe perfil.html en templates
    }

    /**
     * Maneja la actualización del perfil del jugador.
     */
    @PostMapping("/perfil")
    public String modificarPerfil(@Valid @ModelAttribute Jugador jugador, org.springframework.validation.BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("jugador", jugador);
            return "perfil"; // Retorna a la vista de perfil con mensajes de error
        }

        // Obtener el jugador actual autenticado
        String nombreUsuario = authentication.getName();
        Jugador jugadorActual = jugadorService.obtenerJugadorPorNombreUsuario(nombreUsuario);

        // Actualizar el perfil
        ResponseEntity<String> response = jugadorService.modificarPerfil(jugadorActual.getId(), jugador);

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", response.getBody());
        } else {
            model.addAttribute("errorMessage", response.getBody());
        }

        // Obtener el jugador actualizado
        Jugador jugadorActualizado = jugadorService.obtenerJugadorPorNombreUsuario(nombreUsuario);
        model.addAttribute("jugador", jugadorActualizado);
        return "perfil";
    }
    @Autowired
    private TorneoService torneoService;

    @GetMapping("/main")
    public String mostrarPaginaPrincipal(Model model, Authentication authentication) {
        String nombreUsuario = authentication.getName();
        Jugador jugador = jugadorService.obtenerJugadorPorNombreUsuario(nombreUsuario);
        model.addAttribute("jugador", jugador);

        // Obtener torneos disponibles y añadir al modelo
        List<Torneo> torneos = torneoService.obtenerTorneosDisponibles();
        model.addAttribute("torneos", torneos);

        return "main"; // Asegúrate de que existe main.html en templates
    }


}
