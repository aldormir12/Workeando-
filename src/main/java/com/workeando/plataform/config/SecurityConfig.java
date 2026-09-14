package com.workeando.plataform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http //rutas que no requieren login
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/usuarios/**", "/css/**", "/img/**", "/login", "/h2-console/**")
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .permitAll()
                .successHandler(customAuthenticationSuccessHandler()) // Handler redirije a la página segun los roles
            )
            .logout(logout -> logout 
                .logoutSuccessUrl("/login?logout=true")//al cerrar sesion se redirige a la pagina del login
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    // Bean para cifrado de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Custom AuthenticationSuccessHandler para redirigir según el rol
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().toString();

            if (role.contains("ROLE_FREELANCER")) {
                response.sendRedirect("/free"); // Redirige a la página freelancer (free.html)
            } else if (role.contains("ROLE_EMPLEADOR")) {
                response.sendRedirect("/emple"); // Redirige a la página empleador (emple.html)
            } else {
                response.sendRedirect("/"); // Redirige a la página principal si no se encuentra un rol válido
            }
        };
    }
}
