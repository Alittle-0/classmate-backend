package com.devteam.identityservice.controller;


import com.devteam.identityservice.dto.request.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.response.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.request.RegistrationRequestDTO;
import com.devteam.identityservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity/auth")
@Tag(name = "Authentication", description = "Identity - Authentication service")
public class AuthenticationController {

    final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @Valid
            @RequestBody
            final AuthenticationRequestDTO requestDTO,
            final HttpServletResponse response) {
        return ResponseEntity.ok(this.authenticationService.login(requestDTO, response));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid
            @RequestBody
            final RegistrationRequestDTO requestDTO) {
        this.authenticationService.register(requestDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refresh(
            @CookieValue(name = "refreshToken", required = true)
            final String refreshToken,
            final HttpServletResponse response) {
        System.out.println("Refresh token: " + refreshToken);
        return ResponseEntity.ok(this.authenticationService.refreshToken(refreshToken, response));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(final HttpServletResponse response) {
        this.authenticationService.logout(response);
    }

}
