// src/main/java/com/ISII/gestion_torneos_tenis/service/JugadorService.java

package com.ISII.gestion_torneos_tenis.service;

import com.ISII.gestion_torneos_tenis.model.Jugador;
import com.ISII.gestion_torneos_tenis.model.VerificationToken;
import com.ISII.gestion_torneos_tenis.repository.JugadorRepository;
import com.ISII.gestion_torneos_tenis.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JugadorService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(JugadorService.class);

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;



    /**
     * Registra un nuevo jugador y envía un correo de verificación.
     */
    public ResponseEntity<String> registerJugador(Jugador jugador) {
        // Validaciones previas (por ejemplo, verificar si el nombre de usuario ya existe)
        if (jugadorRepository.findByNombreUsuario(jugador.getNombreUsuario()).isPresent()) {
            return new ResponseEntity<>("El nombre de usuario ya está en uso.", HttpStatus.BAD_REQUEST);
        }

        // Encriptar la contraseña
        String encodedPassword = passwordEncoder.encode(jugador.getContrasena());
        jugador.setContrasena(encodedPassword);
        jugador.setEstadoVerificacion(false); // Por defecto no verificado

        // Guardar el jugador
        Jugador nuevoJugador = jugadorRepository.save(jugador);
        logger.info("Jugador registrado exitosamente: '{}'", nuevoJugador.getNombreUsuario());

        // Generar enlace de verificación
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, nuevoJugador, LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);
        logger.debug("Token de verificación generado para '{}': {}", nuevoJugador.getNombreUsuario(), token);

        String enlaceVerificacion = "http://localhost:8080" + "/jugadores/verificar?token=" + token;

        // Enviar correo de verificación
        boolean emailEnviado = emailService.enviarCorreoVerificacion(nuevoJugador.getCorreoElectronico(), enlaceVerificacion);
        if (emailEnviado) {
            logger.info("Correo de verificación enviado a '{}'.", nuevoJugador.getCorreoElectronico());
            return new ResponseEntity<>("Jugador registrado exitosamente. Por favor, verifica tu correo electrónico.", HttpStatus.OK);
        } else {
            logger.error("Error al enviar correo de verificación a '{}'.", nuevoJugador.getCorreoElectronico());
            return new ResponseEntity<>("Jugador registrado, pero ocurrió un error al enviar el correo de verificación.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifica la cuenta del jugador utilizando el token enviado por correo.
     */
    public ResponseEntity<String> verificarCuenta(String token) {
        logger.debug("Iniciando verificación con token: {}", token);
        Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            logger.warn("Verificación fallida: Token inválido '{}'.", token);
            return new ResponseEntity<>("Token de verificación inválido.", HttpStatus.BAD_REQUEST);
        }

        VerificationToken verificationToken = tokenOpt.get();
        logger.debug("Token encontrado para jugador: {}", verificationToken.getJugador().getNombreUsuario());

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Verificación fallida: Token expirado '{}'.", token);
            return new ResponseEntity<>("Token de verificación expirado.", HttpStatus.BAD_REQUEST);
        }

        Jugador jugador = verificationToken.getJugador();
        jugador.setEstadoVerificacion(true);
        jugadorRepository.save(jugador);
        logger.info("Cuenta verificada exitosamente para '{}'.", jugador.getNombreUsuario());

        // Eliminar el token después de verificar
        verificationTokenRepository.delete(verificationToken);
        logger.debug("Token de verificación eliminado '{}'.", token);

        return new ResponseEntity<>("Cuenta verificada exitosamente. Puedes iniciar sesión ahora.", HttpStatus.OK);
    }

    /**
     * Recupera la contraseña del jugador y envía una nueva contraseña por correo.
     */
    public ResponseEntity<String> recuperarContrasena(String correoElectronico) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findByCorreoElectronico(correoElectronico);
        if (jugadorOpt.isEmpty()) {
            logger.warn("Recuperación de contraseña fallida: Correo electrónico no encontrado '{}'.", correoElectronico);
            return new ResponseEntity<>("Correo electrónico no encontrado.", HttpStatus.BAD_REQUEST);
        }

        Jugador jugador = jugadorOpt.get();

        // Generar una nueva contraseña
        String nuevaContrasena = generarContrasenaAleatoria(8); // Contraseña de 8 caracteres
        String encodedPassword = passwordEncoder.encode(nuevaContrasena);
        jugador.setContrasena(encodedPassword);
        jugadorRepository.save(jugador);
        logger.info("Nueva contraseña generada para '{}'.", jugador.getNombreUsuario());

        // Enviar correo con la nueva contraseña
        boolean emailEnviado = emailService.enviarCorreoRecuperacionContrasena(jugador.getCorreoElectronico(), nuevaContrasena);
        if (emailEnviado) {
            logger.info("Correo de recuperación enviado a '{}'.", jugador.getCorreoElectronico());
            return new ResponseEntity<>("Se ha enviado una nueva contraseña a tu correo electrónico.", HttpStatus.OK);
        } else {
            logger.error("Error al enviar correo de recuperación a '{}'.", jugador.getCorreoElectronico());
            return new ResponseEntity<>("Ocurrió un error al enviar el correo de recuperación.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera una contraseña aleatoria de la longitud especificada.
     */
    private String generarContrasenaAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        StringBuilder contrasena = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            int index = (int) (Math.random() * caracteres.length());
            contrasena.append(caracteres.charAt(index));
        }
        return contrasena.toString();
    }

    /**
     * Modifica el perfil del jugador.
     */
    public ResponseEntity<String> modificarPerfil(Long id, Jugador jugador) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(id);
        if (jugadorOpt.isEmpty()) {
            logger.warn("Actualización de perfil fallida: Jugador no encontrado con ID '{}'.", id);
            return new ResponseEntity<>("Jugador no encontrado.", HttpStatus.BAD_REQUEST);
        }

        Jugador jugadorActual = jugadorOpt.get();
        jugadorActual.setNombre(jugador.getNombre());
        jugadorActual.setApellidos(jugador.getApellidos());
        jugadorActual.setCorreoElectronico(jugador.getCorreoElectronico());

        // Solo actualizar la contraseña si se proporciona una nueva
        if (jugador.getContrasena() != null && !jugador.getContrasena().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(jugador.getContrasena());
            jugadorActual.setContrasena(encodedPassword);
            logger.debug("Contraseña actualizada para '{}'.", jugadorActual.getNombreUsuario());
        }

        jugadorActual.setTelefono(jugador.getTelefono());

        jugadorRepository.save(jugadorActual);
        logger.info("Perfil actualizado exitosamente para '{}'.", jugadorActual.getNombreUsuario());

        return new ResponseEntity<>("Perfil actualizado exitosamente.", HttpStatus.OK);
    }

    /**
     * Implementación de UserDetailsService para Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario: '{}'", nombreUsuario);
        Jugador jugador = jugadorRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con nombre de usuario: '{}'", nombreUsuario);
                    return new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + nombreUsuario);
                });

        if (!jugador.isEstadoVerificacion()) {
            logger.warn("Usuario '{}' no verificado.", nombreUsuario);
            throw new DisabledException("Cuenta no verificada. Por favor, verifica tu correo electrónico.");
        }

        logger.debug("Usuario '{}' cargado exitosamente.", nombreUsuario);
        return new User(
                jugador.getNombreUsuario(),
                jugador.getContrasena(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Obtiene un jugador por su nombre de usuario.
     */
    public Jugador obtenerJugadorPorNombreUsuario(String nombreUsuario) {
        return jugadorRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));
    }

    /**
     * Obtiene un jugador por su ID.
     */
    public Jugador obtenerJugadorPorId(Long id) {
        return jugadorRepository.findById(id).orElse(null);
    }

}
