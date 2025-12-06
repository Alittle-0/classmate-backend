package com.devteam.identityservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("Quản trị viên"),
    STUDENT("Sinh viên"),
    TEACHER("Giảng viên");

    private final String displayName;
}
