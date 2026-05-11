
package com.CourierManagement.AdminService.Security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.CourierManagement.AdminService.Config.JwtUtil;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

 private final JwtUtil jwtUtil;

 @Override
 protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain)
         throws ServletException, IOException {

     String email = request.getHeader("X-User-Email");
     String role  = request.getHeader("X-User-Role");

     // If headers missing — try parsing Bearer token directly (Feign calls)
     if (email == null || role == null) {
         String authHeader = request.getHeader("Authorization");
         if (authHeader != null && authHeader.startsWith("Bearer ")) {
             String token = authHeader.substring(7);
             try {
                 if (jwtUtil.validateToken(token)) {
                     email = jwtUtil.extractEmail(token);
                     role  = jwtUtil.extractRole(token);
                 }
             } catch (JwtException e) {
                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 return;
             }
         }
     }

     if (email != null && role != null) {
         String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
         UsernamePasswordAuthenticationToken auth =
             new UsernamePasswordAuthenticationToken(
                 email, null,
                 List.of(new SimpleGrantedAuthority(springRole))
             );
         SecurityContextHolder.getContext().setAuthentication(auth);
     }

     filterChain.doFilter(request, response);
 }
}