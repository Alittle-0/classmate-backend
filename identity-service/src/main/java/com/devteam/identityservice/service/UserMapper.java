package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.request.RegistrationRequestDTO;
import com.devteam.identityservice.dto.request.ProfileUpdateRequestDTO;
import org.apache.commons.lang3.StringUtils;
import com.devteam.identityservice.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void mergerUserInfo(User savedUser, ProfileUpdateRequestDTO request) {

        if (StringUtils.isNoneBlank(request.getFirstname()) && !savedUser.getFirstname().equals(request.getFirstname())) {
            savedUser.setFirstname(request.getFirstname());
        }

        if (StringUtils.isNoneBlank(request.getLastname()) && !savedUser.getLastname().equals(request.getLastname())) {
            savedUser.setLastname(request.getLastname());
        }

        if (request.getRole() != null && savedUser.getRole() != (request.getRole())) {
            savedUser.setRole(request.getRole());
        }

    }

    public User toUser(RegistrationRequestDTO requestDTO) {
        return User.builder()
                .firstname(requestDTO.getFirstname())
                .lastname(requestDTO.getLastname())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(requestDTO.getRole())
                .active(true)
                .build();
    }

}
