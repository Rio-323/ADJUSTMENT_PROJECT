package com.example.filter;

import com.example.dto.UserResponse;
import com.example.client.UserServiceClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String header = request.getHeader("Authorization");
            logger.debug("Authorization Header: " + header);

            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            if (authentication == null) {
                logger.error("Authentication is null, setting response status to UNAUTHORIZED");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            try {
                token = token.replace("Bearer ", "");
                logger.debug("Token after replace: " + token);

                Claims claims = Jwts.parser()
                        .setSigningKey(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);
                logger.debug("Claims from token: userId=" + userId + ", role=" + role);

                if (userId != null && role != null && !role.isEmpty()) {
                    UserResponse user = userServiceClient.getUserDetailsWithLogging("Bearer " + token);
                    logger.debug("User details from UserServiceClient: " + user);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    return new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
                } else {
                    logger.warn("User ID or Role is null or empty");
                }
            } catch (SignatureException e) {
                logger.error("Invalid JWT signature: ", e);
            } catch (Exception e) {
                logger.error("Token parsing error: ", e);
            }
            return null;
        }
        return null;
    }
}
