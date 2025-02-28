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
        String method = exchange.getRequest().getMethod().name(); // Get HTTP method (GET, POST, PUT, etc.)

        // 1. Create Organization (POST)
        if (path.equals("/api/v1/organizations") && method.equals("POST")) {
            return "com.rjproj.memberapp.permission.organization.createOwn";
        }

        // 2. Get My Organization by ID (GET) - The '/current' endpoint
        if (path.matches("^/api/v1/organizations/[0-9a-fA-F-]{36}/current$") && method.equals("GET")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 3. Get Organization by ID (GET) - General Access to organization
        if (path.matches("^/api/v1/organizations/[0-9a-fA-F-]{36}$") && method.equals("GET")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 4. Get Organizations by Multiple IDs (POST)
        if (path.equals("/api/v1/organizations/batch") && method.equals("POST")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 5. Get Organizations (GET) - Pagination and search filters
        if (path.equals("/api/v1/organizations") && method.equals("GET")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 6. Get Organizations by Member ID (GET)
        if (path.matches("^/api/v1/organizations/members/[0-9a-fA-F-]{36}$") && method.equals("GET")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 7. Get Unique Organization Countries (GET)
        if (path.equals("/api/v1/organizations/countries") && method.equals("GET")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 8. Update Organization (PUT)
        if (path.matches("^/api/v1/organizations/[0-9a-fA-F-]{36}$") && method.equals("PUT")) {
            return "com.rjproj.memberapp.permission.organization.viewAll";
        }

        // 9. Update Organization Photo (POST)
        if (path.matches("^/api/v1/organizations/[0-9a-fA-F-]{36}/photo$") && method.equals("POST")) {
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
