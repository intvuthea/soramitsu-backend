package com.example.springjava.filters;

import com.example.springjava.jwt.JwtHelper;
import com.example.springjava.models.User;
import com.example.springjava.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Set current authenticated user context
 * */
public class CustomAccessTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getServletPath().equals("/api/auth/login")) {
            Optional<String> token = parseToken(request);
            if (token.isPresent()) {
                try {
                    String username = jwtHelper.getUsernameFromAccessToken(token.get());
                    User user = userService.getUser(username);
                    UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(upat);
                } catch (Exception e) {
                    System.out.println("..................doFilterInternal.......");
                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");

                    Map<String, String> error = new HashMap<>();
                    error.put("message", e.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
    private Optional<String> parseToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring("Bearer ".length()));
        }

        return Optional.empty();
    }
}
