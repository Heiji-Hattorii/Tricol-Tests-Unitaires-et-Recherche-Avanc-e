package com.gestion.stock.service;

import com.gestion.stock.dto.request.RefreshTokenRequestDTO;
import com.gestion.stock.dto.request.UserRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(UserRequestDTO registerRequest);
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest);
    void logout(String email);
}