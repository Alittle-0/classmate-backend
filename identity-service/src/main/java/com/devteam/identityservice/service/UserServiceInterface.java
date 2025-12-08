package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.PasswordChangeRequestDTO;
import com.devteam.identityservice.dto.ProfileUpdateRequestDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceInterface extends UserDetailsService {

    void updateProfileInformation(ProfileUpdateRequestDTO request, String userId);

    void changePassword(PasswordChangeRequestDTO request, String userId);

    void deactiveAccount(String userId);

    void reactivateAccount(String userId);

    void deleteAccount(String userId);

}
