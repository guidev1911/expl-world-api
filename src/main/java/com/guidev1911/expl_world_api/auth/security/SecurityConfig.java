
package com.guidev1911.expl_world_api.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Rotas públicas de autenticação e documentação
                        .requestMatchers(
                                "/api/v1/auth",
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Consultas públicas
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/categories",
                                "/api/v1/categories/**",
                                "/api/v1/topics",
                                "/api/v1/topics/**",
                                "/api/v1/articles",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections",
                                "/api/v1/article-sections/**"
                        ).permitAll()

                        // Criação exclusiva de ADMIN
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Alteração exclusiva de ADMIN
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Exclusão exclusiva de ADMIN
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Demais requisições exigem autenticação
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
