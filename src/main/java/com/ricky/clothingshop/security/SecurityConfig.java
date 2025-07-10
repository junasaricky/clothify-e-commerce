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
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/", 
                    "/index.html", 
                    "/*.js", 
                    "/*.css", 
                    "/assets/**",
                    "/register",
                    "/forgot-password",
                    "/reset-password",
                    "/shop",
                    "/shop/**",
                    "/cart",
                    "/checkout",
                    "/thank-you",
                    "/payment/**",
                    "/my-orders",
                    "/address/**",
                    "/account",
                    "/account/**",

                    // Admin routes
                    "/admin/dashboard",
                    "/admin/add-product",
                    "/admin/view-products",
                    "/admin/edit-product/**",
                    "/admin/view-orders",
                    "/admin/view-users",
                    "/admin/add-admin",
                    "/admin/admin-settings"
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://clothify-e-commerce.onrender.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
