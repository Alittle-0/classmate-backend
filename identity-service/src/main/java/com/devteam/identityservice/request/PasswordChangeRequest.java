package com.devteam.identityservice.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

}
