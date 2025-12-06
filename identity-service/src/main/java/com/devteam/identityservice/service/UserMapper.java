package com.devteam.identityservice.service;

import com.devteam.identityservice.request.ProfileUpdateRequest;
import org.apache.commons.lang3.StringUtils;
import com.devteam.identityservice.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public void mergerUserInfo(User savedUser, ProfileUpdateRequest request) {

        if (StringUtils.isNoneBlank(request.getFullName()) && !savedUser.getFullName().equals(request.getFullName())) {
            savedUser.setFullName(request.getFullName());
        }

        if (StringUtils.isNoneBlank(request.getEmail()) && !savedUser.getEmail().equals(request.getEmail())) {
            savedUser.setEmail(request.getEmail());
        }

        if (request.getDateOfBirth() != null && !savedUser.getDateOfBirth().isEqual(request.getDateOfBirth())) {
            savedUser.setDateOfBirth(request.getDateOfBirth());
        }

    }
}
