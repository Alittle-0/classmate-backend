package com.devteam.identityservice.controller;


import com.devteam.identityservice.dto.AuthenticationRequestDTO;
import com.devteam.identityservice.dto.AuthenticationResponseDTO;
import com.devteam.identityservice.dto.RefreshRequest;
import com.devteam.identityservice.dto.RegistrationRequestDTO;
import com.devteam.identityservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
            final AuthenticationRequestDTO requestDTO) {
        System.out.println(requestDTO);
        return ResponseEntity.ok(this.authenticationService.login(requestDTO));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid
            @RequestBody
            final RegistrationRequestDTO requestDTO) {
        System.out.println(requestDTO);
        this.authenticationService.register(requestDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refresh(
            @Valid
            @RequestBody
            final RefreshRequest request) {
        return ResponseEntity.ok(this.authenticationService.refreshToken(request));
    }

}
