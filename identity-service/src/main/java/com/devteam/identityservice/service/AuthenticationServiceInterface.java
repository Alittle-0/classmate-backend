package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.request.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.response.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.request.RegistrationRequestDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationServiceInterface {

    AuthenticationResponseDTO login(AuthenticationRequestDTO requestDTO, HttpServletResponse response);

    void register(RegistrationRequestDTO requestDTO);

    AuthenticationResponseDTO refreshToken(String refreshToken, HttpServletResponse response);

    void logout(HttpServletResponse response);

}
