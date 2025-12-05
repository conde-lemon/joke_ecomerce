package com.example.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilitar CORS con configuración global
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Deshabilitar CSRF para endpoints API (necesario para Vue.js)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(requests -> requests
                        // Endpoints públicos de la API
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()  // Permitir todos los endpoints de auth

                        // Endpoints protegidos de la API (requieren autenticación)
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Rutas tradicionales de Thymeleaf
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/", "/home", "/catalog", "/product/**", "/css/**", "/img/**", "/login", "/register").permitAll()

                        .anyRequest().authenticated()
                )
                // Configurar manejo de autenticación para APIs (sin redirección)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Para rutas API, devolver 401 sin redirección
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"No autenticado\"}");
                            } else {
                                // Para otras rutas, redirigir al login
                                response.sendRedirect("/login");
                            }
                        })
                )
                // Form login solo para rutas tradicionales (Thymeleaf), no para API
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        // Spring Security usará automáticamente nuestro CustomUserDetailsService
        return http.build();
    }

    // --- BEAN DE PASSWORD ENCODER ---
    // Lo definimos como un Bean para poder inyectarlo en otras partes de la aplicación,
    // como en EcommerceApplication para encriptar las contraseñas iniciales.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}