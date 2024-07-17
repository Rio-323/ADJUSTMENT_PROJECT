package com.example.filter;

import com.example.dto.UserResponse;
import com.example.client.UserServiceClient;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Environment env;
    private final UserServiceClient userServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(Environment env, UserServiceClient userServiceClient) {
        this.env = env;
        this.userServiceClient = userServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            try {
                token = token.replace("Bearer ", "");
                Claims claims = Jwts.parser()
                        .setSigningKey(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                if (userId != null && role != null) {
                    UserResponse user = userServiceClient.getUserDetailsWithLogging("Bearer " + token);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
                }
            } catch (JwtException | IllegalArgumentException e) {
                logger.error("Token parsing error: ", e);
            }
            return null;
        }
        return null;
    }
}