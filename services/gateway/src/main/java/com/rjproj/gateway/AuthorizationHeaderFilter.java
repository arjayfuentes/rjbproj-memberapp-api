package com.rjproj.gateway;

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

            try {
                if (token != null && jwtUtil.validateToken(token)) {
                    exchange.getRequest().mutate().header("Authorization", "Bearer " + token).build();
                    return chain.filter(exchange);
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

}
