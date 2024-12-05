package com.ISII.gestion_torneos_tenis.config;

import com.ISII.gestion_torneos_tenis.exception.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig  {

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Método de configuración de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Protección CSRF
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Configuración de autorizaciones
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(
                                "/",
                                "/main",
                                "/jugadores/register",
                                "/jugadores/verificar",
                                "/jugadores/recuperar",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // Rutas de administrador
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configuración del formulario de inicio de sesión
                .formLogin(form -> form
                        .loginPage("/jugadores/login")
                        .loginProcessingUrl("/jugadores/login")
                        .usernameParameter("nombreUsuario")
                        .passwordParameter("contrasena")
                        .defaultSuccessUrl("/main", true)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                // Configuración de cierre de sesión
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/jugadores/logout"))
                        .logoutSuccessUrl("/jugadores/login?logout=true")
                        .permitAll()
                )
                // Configuración de headers
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
