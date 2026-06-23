package com.heraim.zelix.common.config;

import com.heraim.zelix.common.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // fully public - no token needed at all
                        .requestMatchers("/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh").permitAll()

                        // public GETs - same path prefix has protected non-GET routes, so method must be specified
                        .requestMatchers(HttpMethod.GET,
                                "/api/stores/{id}",
                                "/api/stores/slug/{slug}",
                                "/api/stores/search",
                                "/api/products/{id}",
                                "/api/products/search",
                                "/api/categories/**"
                        ).permitAll()

                        // everything else requires authentication
                        // (role-specific checks like Vendor-only, Owner-only, Admin-only
                        // are enforced at the method/service level, not here)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}