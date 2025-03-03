package com.musement.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        // allow all requests to the root path
                        .requestMatchers("/").permitAll()

                        // get requests to /api/** are available to all authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                        // concert management is only available to admins
                        .requestMatchers(HttpMethod.POST, "/api/concerts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/concerts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/concerts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/concerts/**").hasRole("ADMIN")

                        // other requests to /api/** are available to all authenticated users
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized");
                            response.flushBuffer();
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Forbidden");
                            response.flushBuffer();
                        })
                )
                .build();

    }

}