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

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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
                        .requestMatchers("/api/v1/membership/requestMembership/**").permitAll()
                        .requestMatchers("/api/v1/membership/createMembershipForCurrentMember/**").permitAll()
                        .requestMatchers("/api/v1/membership/getMembershipByMemberIdAndOrganizationId/**").permitAll()
                        .requestMatchers("/api/v1/membership/getOrganizationByMemberId/**").permitAll()
                        .requestMatchers("/api/v1/membership-type/createMembershipTypes").permitAll()
                        .requestMatchers("/api/v1/membership-type/findAllMembershipTypeValidity").permitAll()
                        .requestMatchers("/api/v1/member/createDefaultAdminOrganizationRoleForOwner").permitAll()
                        .requestMatchers("/api/v1/member/organization/**").hasAnyAuthority("com.rjproj.memberapp.permission.user.viewOrgAll", "com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/member/organizationPage/**").hasAnyAuthority("com.rjproj.memberapp.permission.user.viewOrgAll", "com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/member/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/membership/getOrganizationByMemberId/**").hasAnyAuthority("com.rjproj.memberapp.permission.organization.viewOwn, com.rjproj.memberapp.permission.organization.viewAll, com.rjproj.memberapp.permission.organization.viewAll")
                        .requestMatchers("/api/v1/membership/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")
                        .requestMatchers("/api/v1/membership-type/**").hasAuthority("com.rjproj.memberapp.permission.user.viewAll")
                        .anyRequest()
                        .authenticated())
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
