package com.devteam.identityservice.controller;

import com.devteam.identityservice.model.User;
import com.devteam.identityservice.request.PasswordChangeRequest;
import com.devteam.identityservice.request.ProfileUpdateRequest;
import com.devteam.identityservice.service.AuthenticationService;
import com.devteam.identityservice.service.AuthenticationServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/identity")
@RequiredArgsConstructor
@Tag(name = "User", description = "Identify Service")
public class AuthenticationController {

    @Autowired
    private final AuthenticationServiceInterface authenticationService;

//    Health API
    @GetMapping("health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API is healthy");

    }

    @PatchMapping("me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfileInformation(
            @RequestBody @Valid final ProfileUpdateRequest request,
            final Authentication principal
            ) {
        this.authenticationService.updateProfileInformation(request, getUserId(principal));
    }

    @PostMapping("me/password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid final PasswordChangeRequest request,
            final Authentication principal
            ) {
        this.authenticationService.changePassword(request, getUserId(principal));
    }

    @PostMapping("me/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivateAccount(
            final Authentication principal
            ) {
        this.authenticationService.deactiveAccount(getUserId(principal));
    }

    @PostMapping("me/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactiveAccount(final Authentication principal) {
        this.authenticationService.reactivateAccount(getUserId(principal));
    }

    @DeleteMapping("me/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAccount(final Authentication principal) {
        this.authenticationService.deleteAccount(getUserId(principal));
    }

    private String getUserId(Authentication principal) {
        return ((User) principal.getPrincipal()).getId();
    }


}
