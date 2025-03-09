package com.musement.backend.config;

import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // allow all requests to the root path
                        .requestMatchers("/").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

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
                .httpBasic(withDefaults())
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

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User userEntity = userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(userEntity.getUsername())
                    .password(userEntity.getPassword())
                    .roles("USER")
                    .build();
        };
    }
}
