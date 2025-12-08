package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.RefreshRequest;
import com.devteam.identityservice.dto.RegistrationRequestDTO;

public interface AuthenticationServiceInterface {

    AuthenticationResponseDTO login(AuthenticationRequestDTO requestDTO);

    void register(RegistrationRequestDTO requestDTO);

    AuthenticationResponseDTO refreshToken(RefreshRequest request);

}
