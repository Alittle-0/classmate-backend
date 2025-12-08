package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.RegistrationRequestDTO;
import com.devteam.identityservice.dto.ProfileUpdateRequestDTO;
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

        if (StringUtils.isNoneBlank(request.getFirstName()) && !savedUser.getFirstName().equals(request.getFirstName())) {
            savedUser.setFirstName(request.getFirstName());
        }

        if (StringUtils.isNoneBlank(request.getLastName()) && !savedUser.getLastName().equals(request.getLastName())) {
            savedUser.setLastName(request.getLastName());
        }

        if (StringUtils.isNoneBlank(request.getEmail()) && !savedUser.getEmail().equals(request.getEmail())) {
            savedUser.setEmail(request.getEmail());
        }

//        if (request.getDateOfBirth() != null && !savedUser.getDateOfBirth().isEqual(request.getDateOfBirth())) {
//            savedUser.setDateOfBirth(request.getDateOfBirth());
//        }

    }

    public User toUser(RegistrationRequestDTO requestDTO) {
        return User.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(requestDTO.getRole())
                .build();
    }
}
