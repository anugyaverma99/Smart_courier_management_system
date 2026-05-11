package com.CourierManagement.AuthService.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader("X-User-Email");  // ① injected by gateway
        String role  = request.getHeader("X-User-Role");   // ② injected by gateway

        if (email != null && role != null) {
            String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role; // ③
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority(springRole))
                );
            SecurityContextHolder.getContext().setAuthentication(auth); // ④
        }
//        Pass request to controller normally.

        filterChain.doFilter(request, response); // ⑤
    }
}
