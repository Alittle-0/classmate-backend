package com.devteam.identityservice.controller;

import com.devteam.identityservice.dto.response.UserResponseDTO;
import com.devteam.identityservice.model.User;
import com.devteam.identityservice.dto.request.PasswordChangeRequestDTO;
import com.devteam.identityservice.dto.request.ProfileUpdateRequestDTO;
import com.devteam.identityservice.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity/me")
@RequiredArgsConstructor
@Tag(name = "User", description = "Identify - User Service")
public class UserController {

    private final UserServiceInterface userService;

    @GetMapping("/")
    public ResponseEntity<UserResponseDTO> getProfile(
            final Authentication principal) {
        return ResponseEntity.ok(this.userService.getProfile(getUserId(principal)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }


    @PatchMapping("/")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfileInformation(
            @RequestBody @Valid final ProfileUpdateRequestDTO request,
            final Authentication principal
            ) {
        this.userService.updateProfileInformation(request, getUserId(principal));
    }

    @PostMapping("/password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid final PasswordChangeRequestDTO request,
            final Authentication principal
            ) {
        this.userService.changePassword(request, getUserId(principal));
    }

    @PostMapping("/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivateAccount(
            final Authentication principal
            ) {
        this.userService.deactiveAccount(getUserId(principal));
    }

    @PostMapping("/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactiveAccount(final Authentication principal) {
        this.userService.reactivateAccount(getUserId(principal));
    }

    @DeleteMapping("/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAccount(final Authentication principal) {
        this.userService.deleteAccount(getUserId(principal));
    }

    private String getUserId(Authentication principal) {
        return ((User) principal.getPrincipal()).getId();
    }


}
