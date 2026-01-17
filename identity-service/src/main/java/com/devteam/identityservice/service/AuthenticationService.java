package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.request.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.response.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.request.RegistrationRequestDTO;
import com.devteam.identityservice.exception.BusinessException;
import com.devteam.identityservice.exception.ErrorCode;
import com.devteam.identityservice.model.Role;
import com.devteam.identityservice.model.User;
import com.devteam.identityservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
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
    public AuthenticationResponseDTO login(AuthenticationRequestDTO requestDTO, HttpServletResponse response) {

        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
        );

        final User user = (User) authentication.getPrincipal();
        final String accessToken = this.jwtService.generateAccessToken(user);
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .build();

        // Add cookie to response
        response.addHeader("Set-Cookie", cookie.toString());

        final String tokenType = "Bearer";

        return AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
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
    public AuthenticationResponseDTO refreshToken(String refreshToken, HttpServletResponse response) {
        final String newAccessToken = this.jwtService.refreshAccessToken(refreshToken);

        // Keep the refresh token in cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .build();
        response.addHeader("Set-Cookie", cookie.toString());

        final String tokenType = "Bearer";
        return AuthenticationResponseDTO.builder()
                .accessToken(newAccessToken)
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

    @Override
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(0)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
        log.info("User logged out successfully, refresh token cookie cleared");
    }

}
