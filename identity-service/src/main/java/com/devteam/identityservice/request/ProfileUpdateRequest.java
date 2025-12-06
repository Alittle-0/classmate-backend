package com.devteam.identityservice.service;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    private String fullName;
    private String email;
    private LocalDate dateOfBirth;

}
