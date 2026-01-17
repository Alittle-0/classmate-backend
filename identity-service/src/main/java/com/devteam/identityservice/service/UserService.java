package com.devteam.identityservice.service;

import com.devteam.identityservice.dto.response.UserResponseDTO;
import com.devteam.identityservice.exception.BusinessException;
import com.devteam.identityservice.exception.ErrorCode;
import com.devteam.identityservice.repository.UserRepository;
import com.devteam.identityservice.dto.request.PasswordChangeRequestDTO;
import com.devteam.identityservice.dto.request.ProfileUpdateRequestDTO;
import lombok.extern.slf4j.Slf4j;
import com.devteam.identityservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    public User findUserById(String userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTFOUND, userId));
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return this.userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));
    }

    @Override
    public UserResponseDTO getProfile(String userId) {
        final User user = findUserById(userId);
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    @Override
    public void updateProfileInformation(ProfileUpdateRequestDTO request, String userId) {
        final User savedUser = findUserById(userId);
        this.userMapper.mergerUserInfo(savedUser, request);
        this.userRepository.save(savedUser);
    }

    @Override
    public void changePassword(PasswordChangeRequestDTO request, String userId) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
        }

        final User savedUser = findUserById(userId);

        if (!this.passwordEncoder.matches(request.getCurrentPassword(), savedUser.getPassword()))
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);

        final String encodedPassword = this.passwordEncoder.encode(request.getNewPassword());
        savedUser.setPassword(encodedPassword);
        this.userRepository.save(savedUser);

    }

    @Override
    public void deactiveAccount(String userId) {
        final User user = findUserById(userId);

        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }

        user.setEnabled(false);
        user.setActive(false);
        this.userRepository.save(user);
    }

    @Override
    public void reactivateAccount(String userId) {
        final User user = findUserById(userId);

        if (user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_ACTIVATED);
        }

        user.setEnabled(true);
        user.setActive(true);
        this.userRepository.save(user);
    }

//    Not use this
    @Override
    public void deleteAccount(String userId) {
        final User user = findUserById(userId);
        this.userRepository.delete(user);
    }

}