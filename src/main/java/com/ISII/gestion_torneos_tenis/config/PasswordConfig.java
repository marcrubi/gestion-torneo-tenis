package com.ISII.gestion_torneos_tenis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean(name = "customPasswordEncoder")
    public BCryptPasswordEncoder customPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
