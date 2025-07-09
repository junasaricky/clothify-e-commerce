package com.ricky.clothingshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // Password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager using AuthenticationConfiguration (no deprecated code)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Security filter chain (JWT + role-based auth)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/", 
                    "/index.html", 
                    "/*.js", 
                    "/*.css", 
                    "/assets/**"
                ).permitAll()
                .requestMatchers("/api/auth/**", "/api/products/**").permitAll()  // public
                .requestMatchers("/api/admin/**").hasRole("ADMIN")               // admin-only
                .requestMatchers("/api/cart/**", "/api/orders/**").hasAnyAuthority("ROLE_CUSTOMER") // customer-only
                .requestMatchers("/error").permitAll()
                .requestMatchers("/images/**").permitAll() 
                .anyRequest().authenticated())                                   // rest must login
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))        // JWT = stateless
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // our JWT filter
            
        return http.build();
    }
}
