package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.RefreshTokenRequestDTO;
import com.gestion.stock.dto.request.UserRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;
import com.gestion.stock.entity.UserApp;
import com.gestion.stock.exception.DuplicateResourceException;
import com.gestion.stock.repository.UserAppRepository;
import com.gestion.stock.security.CustomUserDetailsService;
import com.gestion.stock.security.JwtUtil;
import com.gestion.stock.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserAppRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private AuditService auditService;
    
    @Mock
    private CustomUserDetailsService userDetailsService;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private UserRequestDTO userRequest;
    private UserApp userApp;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setEmail("test@tricol.com");
        userRequest.setPassword("password123");
        
        userApp = new UserApp();
        userApp.setId(1L);
        userApp.setEmail("test@tricol.com");
        userApp.setPassword("encodedPassword");
        userApp.setEnabled(true);
    }

    @Test
    void register_ShouldCreateUser_WhenEmailNotExists() {
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserApp.class))).thenReturn(userApp);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtUtil.generateAccessToken(any())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");

        AuthResponseDTO result = authService.register(userRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals("test@tricol.com", result.getEmail());
        verify(userRepository).save(any(UserApp.class));
        verify(auditService).logAction(eq("REGISTER"), eq("USER"), anyString(), anyString(), anyString(), isNull());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void refreshToken_ShouldReturnNewTokens_WhenValidToken() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("validRefreshToken");
        
        when(jwtUtil.validateToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("validRefreshToken")).thenReturn("test@tricol.com");
        when(userRepository.findByEmail("test@tricol.com")).thenReturn(Optional.of(userApp));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtUtil.generateAccessToken(any())).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("newRefreshToken");

        AuthResponseDTO result = authService.refreshToken(request);
        assertNotNull(result);
        assertEquals("newAccessToken", result.getAccessToken());
        assertEquals("newRefreshToken", result.getRefreshToken());
        assertEquals("test@tricol.com", result.getEmail());
    }

    @Test
    void refreshToken_ShouldThrowException_WhenInvalidToken() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("invalidToken");
        
        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
    }

    @Test
    void logout_ShouldLogAuditAction() {
        String email = "test@tricol.com";
        authService.logout(email);
        verify(auditService).logAction("LOGOUT", "USER", null, email, "Déconnexion", null);
    }
}