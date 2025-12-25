package com.gestion.stock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.stock.dto.request.LoginRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final FilterExceptionHandler exceptionHandler;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws AuthenticationException {
        try {
            LoginRequestDTO loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture des données de connexion", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                          FilterChain chain, Authentication authResult) throws IOException {
        String accessToken = jwtUtil.generateAccessToken(authResult);
        String refreshToken = jwtUtil.generateRefreshToken(authResult.getName());

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("tokenType", "Bearer");
        tokens.put("email", authResult.getName());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                            AuthenticationException failed) throws IOException {
        try {
            exceptionHandler.handleException(response, failed);
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json");
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Échec de l'authentification");
            error.put("message", failed.getMessage());
            
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }
}