package com.amdox.nexushr.payroll.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Mirrors auth-service's JwtAuthFilter exactly:
 * - same Redis blacklist key pattern ("blacklist:" + token) so a logged-out
 *   token issued by auth-service is also rejected here
 * - same claim names (subject = userId, "role" claim)
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    public JwtAuthFilter(JwtService jwtService, StringRedisTemplate redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain chain) throws ServletException, IOException {
    	
    	    System.out.println("========== JWT FILTER ==========");
    	    System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());
    	    System.out.println("Authorization Header: " + request.getHeader("Authorization"));

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Boolean blacklisted = redisTemplate.hasKey("blacklist:" + token);
            if (Boolean.TRUE.equals(blacklisted)) {
                log.debug("Blacklisted token rejected");
                chain.doFilter(request, response);
                return;
            }

            Claims claims = jwtService.validateAndExtract(token);

            System.out.println("JWT VALIDATED");
            System.out.println("Claims: " + claims);

            String userIdStr = claims.getSubject();
            String role = claims.get("role", String.class);
            String tenantIdStr = claims.get("tenantId", String.class);

            AuthenticatedUser principal = new AuthenticatedUser(
                    UUID.fromString(userIdStr),
                    tenantIdStr != null ? UUID.fromString(tenantIdStr) : null,
                    role
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("AUTHENTICATION SET");
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        }catch (Exception e) {
        	 e.printStackTrace();        }

        chain.doFilter(request, response);
    }
}
