package com.rjproj.gateway;

import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    Environment env;

    @Autowired
    JWTUtil jwtUtil;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {
        // Put configuration properties here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String token = extractJwtFromRequest(exchange);
            System.out.println(token);

            try {
                if (token != null && jwtUtil.validateToken(token)) {
                    List<String> permissions = jwtUtil.extractPermissions(token);
                    if (hasPermissionForRequest(exchange, permissions)) {
                        return chain.filter(exchange);
                    } else {
                        return handleUnauthorizedResponse(exchange, "You do not have permission to access this resource.");
                    }
                } else {
                    return handleForbiddenResponse(exchange, "You must log in to access this resource.");
                }
            } catch (SignatureException e) {
                return handleUnauthorizedResponse(exchange, "Unauthorized: Missing or invalid token.");

            }
        };
    }

    private String extractJwtFromRequest(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // Extract token after "Bearer "
        }
        return null;
    }

    private boolean hasPermissionForRequest(ServerWebExchange exchange, List<String> permissions) {
        String requiredPermission = getRequiredPermissionForRequest(exchange);
        return permissions != null && permissions.contains(requiredPermission);
    }

    private String getRequiredPermissionForRequest(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/api/v1/organization/findOrganizationsByIds")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/completeCreateOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewOwn";
        }
        if (path.contains("/api/v1/organization/viewAllOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/saveOrganizations")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/uploadOrganizationImage")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/getOrganizationImages")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/findMyOrganizationById")) {
            return "com.rjproj.memberapp.permission.organization.viewOwn";
        }
        if (path.contains("/api/v1/organization/viewMyOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewOwn";
        }
        if (path.contains("/api/v1/organization/findOrganizationById")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/findOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization")) {
            return "com.rjproj.memberapp.permission.organization.editAll";
        }


        return null;  // Def
    }


    // Handle FORBIDDEN response with custom message in the body
    private Mono<Void> handleForbiddenResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);

        // Set the response body with a custom JSON message
        response.getHeaders().set("Content-Type", "application/json");
        String body = "{ \"error\": \"Access denied\", \"message\": \"" + message + "\" }";  // Correct JSON format

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    // Handle UNAUTHORIZED response with custom message in the body
    private Mono<Void> handleUnauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        // Set the response body with a custom JSON message
        response.getHeaders().set("Content-Type", "application/json");
        String body = "{ \"error\": \"Unauthorized\", \"message\": \"" + message + "\" }";  // Correct JSON format

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

        return response.writeWith(Mono.just(buffer));
    }



}
