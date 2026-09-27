package com.guidev1911.expl_world_api.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Rotas públicas
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Apenas ADMIN pode criar
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Apenas ADMIN pode alterar
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Apenas ADMIN pode excluir
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/**",
                                "/api/v1/topics/**",
                                "/api/v1/articles/**",
                                "/api/v1/article-sections/**"
                        ).hasRole("ADMIN")

                        // Todo o restante exige autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}