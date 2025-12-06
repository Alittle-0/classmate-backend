package com.devteam.identityservice.service;

import com.devteam.identityservice.request.PasswordChangeRequest;
import com.devteam.identityservice.request.ProfileUpdateRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceInterface extends UserDetailsService {

    void updateProfileInformation(ProfileUpdateRequest request, String userId);

    void changePassword(PasswordChangeRequest request, String userId);

    void deactiveAccount(String userId);

    void reactivateAccount(String userId);

    void deleteAccount(String userId);

}
