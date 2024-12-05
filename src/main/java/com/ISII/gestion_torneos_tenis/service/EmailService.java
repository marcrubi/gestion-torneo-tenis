// src/main/java/com/ISII/gestion_torneos_tenis/service/EmailService.java

package com.ISII.gestion_torneos_tenis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo de verificación de cuenta.
     */
    public boolean enviarCorreoVerificacion(String to, String enlaceVerificacion) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verificación de Cuenta - Gestión de Torneos de Tenis");
            message.setText("Hola,\n\n" +
                    "Gracias por registrarte en nuestro sistema. Por favor, verifica tu cuenta haciendo clic en el siguiente enlace:\n" +
                    enlaceVerificacion + "\n\n" +
                    "Este enlace expirará en 24 horas.\n\n" +
                    "Saludos,\n" +
                    "Equipo de Gestión de Torneos de Tenis");
            mailSender.send(message);
            logger.info("Correo de verificación enviado a '{}'.", to);
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar correo de verificación a '{}': {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Envía un correo de recuperación de contraseña.
     */
    public boolean enviarCorreoRecuperacionContrasena(String to, String nuevaContrasena) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Recuperación de Contraseña - Gestión de Torneos de Tenis");
            message.setText("Hola,\n\n" +
                    "Has solicitado restablecer tu contraseña. Tu nueva contraseña es:\n" +
                    nuevaContrasena + "\n\n" +
                    "Por razones de seguridad, te recomendamos que cambies esta contraseña después de iniciar sesión.\n\n" +
                    "Saludos,\n" +
                    "Equipo de Gestión de Torneos de Tenis");
            mailSender.send(message);
            logger.info("Correo de recuperación enviado a '{}'.", to);
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar correo de recuperación a '{}': {}", to, e.getMessage());
            return false;
        }
    }
}
