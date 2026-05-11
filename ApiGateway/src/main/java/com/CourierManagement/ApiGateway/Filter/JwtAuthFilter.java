package com.CourierManagement.ApiGateway.Filter;

import com.CourierManagement.ApiGateway.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Gateway path: " + path);

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (isPublicRoute(path)) {
            System.out.println("Public route — skipping JWT");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing token");
            return rejectRequest(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Invalid token");
            return rejectRequest(exchange);
        }

        String role = jwtUtil.extractRole(token);
        String email = jwtUtil.extractEmail(token);
        System.out.println("Authenticated — email: " + email + ", role: " + role);

        if (path.startsWith("/gateway/admin") && !"ADMIN".equals(role)) {
            System.out.println("Admin route blocked for role: " + role);
            return rejectRequest(exchange);
        }

        // Build brand new mutable headers
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.putAll(exchange.getRequest().getHeaders());
        newHeaders.set("X-User-Email", email);
        newHeaders.set("X-User-Role", role);

        // Wrap using decorator — bypasses the read-only restriction entirely
        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return newHeaders;
            }
        };

        return chain.filter(exchange.mutate().request(decoratedRequest).build());
    }

    private boolean isPublicRoute(String path) {
        return path.contains("/auth/login")  ||
               path.contains("/auth/signup") ||
               path.contains("/auth/users")  ||
               path.contains("/swagger")     ||
               path.contains("/api-docs")    ||
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
