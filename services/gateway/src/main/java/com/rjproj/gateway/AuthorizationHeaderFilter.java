package com.rjproj.gateway;

import java.util.List;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

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

    private String getRequiredPermissionForRequest(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/api/v1/organization/findOrganizationsByIds")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/completeCreateOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewOwn";
        }
        if (path.contains("/api/v1/organization/getOrganizationsByMemberId")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/getAllOrganizations")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/updateOrganization")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/updateOrganizationPhoto")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/organizationCountries")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/findMyOrganizationById")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        if (path.contains("/api/v1/organization/findOrganizationById")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }
        return null;
    }

    private Mono<Void> handleForbiddenResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);

        response.getHeaders().set("Content-Type", "application/json");
        String body = "{ \"error\": \"Access denied\", \"message\": \"" + message + "\" }";  // Correct JSON format

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> handleUnauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        response.getHeaders().set("Content-Type", "application/json");
        String body = "{ \"error\": \"Unauthorized\", \"message\": \"" + message + "\" }";  // Correct JSON format

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    private boolean hasPermissionForRequest(ServerWebExchange exchange, List<String> permissions) {
        String requiredPermission = getRequiredPermissionForRequest(exchange);
        return permissions != null && permissions.contains(requiredPermission);
    }

}
