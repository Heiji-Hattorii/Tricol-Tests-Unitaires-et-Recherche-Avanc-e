package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.RefreshTokenRequestDTO;
import com.gestion.stock.dto.request.UserRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;
import com.gestion.stock.entity.UserApp;
import com.gestion.stock.exception.DuplicateResourceException;
import com.gestion.stock.repository.UserAppRepository;
import com.gestion.stock.security.JwtUtil;
import com.gestion.stock.service.AuthService;
import com.gestion.stock.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    @Override
    public AuthResponseDTO register(UserRequestDTO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Un utilisateur avec cet email existe déjà");
        }

        UserApp user = new UserApp();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        user = userRepository.save(user);

        auditService.logAction("REGISTER", "USER", user.getId().toString(), 
            user.getEmail(), "Inscription d'un nouvel utilisateur", null);

        // Créer une authentification temporaire pour générer les tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );

        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponseDTO(
            accessToken,
            refreshToken,
            "Bearer",
            user.getEmail(),
            null, // Pas de rôle
            Set.of() // Pas de permissions
        );
    }

    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token invalide");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        UserApp user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );

        String newAccessToken = jwtUtil.generateAccessToken(authentication);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        Set<String> permissions = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        return new AuthResponseDTO(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            user.getEmail(),
            user.getRole() != null ? user.getRole().getName() : null,
            permissions
        );
    }

    @Override
    public void logout(String email) {
        auditService.logAction("LOGOUT", "USER", null, 
            email, "Déconnexion", null);
    }
}