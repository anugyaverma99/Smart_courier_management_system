package com.CourierManagement.ApiGateway.Filter;

import com.CourierManagement.ApiGateway.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Gateway path: " + path);

        // Skip JWT for public routes
        if (isPublicRoute(path)) {
            System.out.println("Public route — skipping JWT");
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing token");
            return rejectRequest(exchange);
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            System.out.println("Invalid token");
            return rejectRequest(exchange);
        }

        String role = jwtUtil.extractRole(token);
        System.out.println("Authenticated role: " + role);

        // Block non-admin from admin routes
        if (path.startsWith("/gateway/admin") && !"ADMIN".equals(role)) {
            System.out.println("Admin route blocked for role: " + role);
            return rejectRequest(exchange);
        }

        // Just forward — no header mutation
        return chain.filter(exchange);
    }

    private boolean isPublicRoute(String path) {
        return path.contains("/auth/login") ||
               path.contains("/auth/signup") ||
               path.contains("/swagger") ||
               path.contains("/api-docs") ||
               path.contains("/webjars");
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}