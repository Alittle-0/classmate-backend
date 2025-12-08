package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.RefreshRequest;
import com.devteam.identityservice.dto.RegistrationRequestDTO;
import com.devteam.identityservice.exception.BusinessException;
import com.devteam.identityservice.exception.ErrorCode;
import com.devteam.identityservice.model.Role;
import com.devteam.identityservice.model.User;
import com.devteam.identityservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService implements AuthenticationServiceInterface{

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO requestDTO) {

        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
        );

        final User user = (User) authentication.getPrincipal();
        System.out.println(user);
        final String accessToken = this.jwtService.generateAccessToken(user.getUsername());
        System.out.println(accessToken);
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        System.out.println(refreshToken);
        final String tokenType = "Bearer";

        return AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Override
    @Transactional
    public void register(RegistrationRequestDTO requestDTO) {
        log.debug("Registering user {}", requestDTO);
        checkUserEmail(requestDTO.getEmail());
        checkPasswords(requestDTO.getPassword(), requestDTO.getConfirmPassword());
        checkUserRole(requestDTO.getRole());

        final User user = this.userMapper.toUser(requestDTO);
        log.debug("Saving user {}", user);
        this.userRepository.save(user);
    }

    @Override
    public AuthenticationResponseDTO refreshToken(RefreshRequest request) {
        final String newAccessToken = this.jwtService.refreshAccessToken(request.getRefreshToken());
        final String tokenType = "Bearer";
        return AuthenticationResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType(tokenType).build();
    }

    private void checkPasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
    }

    private void checkUserEmail(String email) {
        final boolean exists = this.userRepository.existsByEmailIgnoreCase(email);
        if (exists) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkUserRole(Role role) {
        if (role == Role.ADMIN)
            throw new BusinessException(ErrorCode.INVALID_ROLE);

    }

}
