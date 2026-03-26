// filter/JwtAuthFilter.java
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Public routes — no token needed
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/gateway/auth/login",
            "/gateway/auth/signup"
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Print path for debugging — remove later
        System.out.println("Incoming request path: " + path);

        // Skip JWT check for public routes
        if (isPublicRoute(path)) {
            System.out.println("Public route — skipping JWT check");
            return chain.filter(exchange);
        }

        // Check Authorization header exists
        String authHeader = exchange.getRequest()
                .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing token for path: " + path);
            return rejectRequest(exchange);
        }

        // Extract and validate token
        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Invalid token for path: " + path);
            return rejectRequest(exchange);
        }

        // Extract role — block non-admin from admin routes
        String role = jwtUtil.extractRole(token);
        if (path.startsWith("/gateway/admin") && !"ADMIN".equals(role)) {
            System.out.println("Admin route blocked for role: " + role);
            return rejectRequest(exchange);
        }

        // Pass user info to downstream service via headers
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Email", jwtUtil.extractEmail(token))
                        .header("X-User-Role", role)
                        .build())
                .build();

        return chain.filter(modifiedExchange);
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream()
                .anyMatch(route -> path.equals(route) || path.startsWith(route));
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