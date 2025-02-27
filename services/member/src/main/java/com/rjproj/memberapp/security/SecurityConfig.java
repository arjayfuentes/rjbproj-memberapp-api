package com.rjproj.memberapp.security;

import com.rjproj.memberapp.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import static com.rjproj.memberapp.exception.MemberErrorMessage.ACCESS_DENIED;
import static com.rjproj.memberapp.exception.MemberErrorMessage.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${keyStore.path}")
    private String keyStorePath;

    @Value("${keyStore.password}")
    private String keyStorePassword;

    @Autowired
    private JWTFilter jwtFilter;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/auth/logout").permitAll()
                        .requestMatchers("/api/v1/auth/getLoginSession").permitAll()
                        .requestMatchers("/api/v1/auth/login/withGoogle").permitAll()
                        .requestMatchers("/api/v1/role/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")

                        //MEMBERSHIPS
                        // Specific permissions for approve, deny, and update membership requests
                        .requestMatchers(
                                "/api/v1/memberships/{membershipId}/approve",
                                "/api/v1/memberships/{membershipId}/deny",
                                "/api/v1/memberships/{membershipId}")
                                    .hasAnyAuthority(
                                            "com.rjproj.memberapp.permission.user.editOrgAll",
                                            "com.rjproj.memberapp.permission.user.editAll")
                        // Specific permissions for viewing and managing memberships within organizations
                        .requestMatchers(
                                "/api/v1/memberships/organizations/{organizationId}/members",
                                "/api/v1/memberships/organizations/{organizationId}/members/pending")
                                        .hasAnyAuthority(
                                                "com.rjproj.memberapp.permission.user.viewOrgAll",
                                                "com.rjproj.memberapp.permission.user.viewAll")
                        // Permissions for creating membership for the current user and requesting membership
                        .requestMatchers(
                                "/api/v1/memberships/current",
                                "/api/v1/memberships/request")
                                        .hasAnyAuthority(
                                                "com.rjproj.memberapp.permission.user.createOwn",
                                                "com.rjproj.memberapp.permission.user.createOrg",
                                                "com.rjproj.memberapp.permission.user.createAll")
                        // Permissions for getting a membership by memberId and organizationId *CHECK
                        .requestMatchers(
                                "/api/v1/memberships/organizations/{organizationId}/members/{memberId}",
                                "/api/v1/memberships/members/{memberId}")
                                        .permitAll()

                        //MEMBERSHIP-TYPES
                        .requestMatchers(
                                "/api/v1/membership-types/validities")
                                        .permitAll()
                        .requestMatchers(
                                "/api/v1/membership-types/**")
                                        .hasAnyAuthority(
                                                "com.rjproj.memberapp.permission.user.viewOrgAll",
                                                "com.rjproj.memberapp.permission.user.viewAll")


                        .requestMatchers("/api/v1/membership-status/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/member/updateMemberDetails").permitAll()












                        .requestMatchers("/api/v1/member/createDefaultAdminOrganizationRoleForOwner").permitAll()
                        .requestMatchers("/api/v1/member/organization/**").hasAnyAuthority("com.rjproj.memberapp.permission.user.viewOrgAll", "com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/member/organizationPage/**").hasAnyAuthority("com.rjproj.memberapp.permission.user.viewOrgAll", "com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/member/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")
                        .anyRequest()
                        .authenticated())
                .oauth2Client(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler())  // Handle 401 Unauthorized
                        .accessDeniedHandler(customAccessDeniedHandler()) // Handle 403 Forbidden
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")  // Specify the logout URL
                        .logoutSuccessHandler((request, response, authentication) -> {
                            jwtFilter.deleteToken();
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_OK);  // Return HTTP 200 for successful logout
                            response.getWriter().write("{\"message\": \"Logout successful\"}");  // Optional success message
                        })
                        .clearAuthentication(true)  // Clear authentication information after logout
                        .deleteCookies("JSESSIONID"))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }


//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://your-frontend-url.com"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

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

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) -> {

            // Log the authenticated user and authorities
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                System.out.println("Authenticated User: " + authentication.getName());
                System.out.println("User Authorities: " + authentication.getAuthorities());
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(String.format(
                    "{\"error\": \"%s\", \"message\": \"You do not have permission to access this resource.\"}",
                    UNAUTHORIZED.getMessage()
            ));
            writer.flush();
        };
    }

}
