package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.request.PasswordChangeRequestDTO;
import com.devteam.identityservice.dto.request.ProfileUpdateRequestDTO;
import com.devteam.identityservice.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceInterface extends UserDetailsService {

    UserResponseDTO getProfile(String userId);

    void updateProfileInformation(ProfileUpdateRequestDTO request, String userId);

    void changePassword(PasswordChangeRequestDTO request, String userId);

    void deactiveAccount(String userId);

    void reactivateAccount(String userId);

    void deleteAccount(String userId);

}
