package com.rjproj.memberapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;

import static com.rjproj.memberapp.model.OrganizationErrorMessage.ACCESS_DENIED;
import static com.rjproj.memberapp.model.OrganizationErrorMessage.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    public SecurityConfig() {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request

                        .requestMatchers("/api/v1/organizations/countries")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.createOwn")

                        .requestMatchers("/api/v1/organizations/{organizationId}/current")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations/{organizationId}")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations/batch")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations/members/{memberId}")
                            .hasAuthority("com.rjproj.memberapp.permission.organization.viewAll")

                        .requestMatchers("/api/v1/organizations/{organizationId}")
                            .hasAnyAuthority("com.rjproj.memberapp.permission.organization.editOrgAll", "com.rjproj.memberapp.permission.organization.editAll")

                        .requestMatchers("/api/v1/organizations/{organizationId}/photo")
                            .hasAnyAuthority("com.rjproj.memberapp.permission.organization.editOrgAll", "com.rjproj.memberapp.permission.organization.editAll")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler())  // Handle 401 Unauthorized
                        .accessDeniedHandler(customAccessDeniedHandler()) // Handle 403 Forbidden
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
            org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                System.out.println("Authenticated User: " + authentication.getName());
                System.out.println("User Authorities: " + authentication.getAuthorities());
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(String.format(
                    "{\"error\": \"%s\", \"message\": \"You do not have permission to access this resource.\"}",
                    UNAUTHORIZED.getMessage()
            ));
            writer.flush();
        };
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(String.format(
                    "{\"error\": \"%s\", \"message\": \"You must log in to access this resource.\"}",
                    ACCESS_DENIED.getMessage()
            ));
            writer.flush();
        };
    }
}
