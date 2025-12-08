package com.devteam.identityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangeRequestDTO {

    @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.CURRENT_PASSWORD.NOT_BLANK")
    @Size(
            min = 10,
            max = 50,
            message = "VALIDATION.CHANGE_PASSWORD.CURRENT_PASSWORD.SIZE"
    )
    @Schema(example = "Black12345_@")
    private String currentPassword;

    @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.NOT_BLANK")
    @Size(
            min = 10,
            max = 50,
            message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.SIZE"
    )
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.WEAK"
    )
    @Schema(example = "Black12345_@")
    private String newPassword;

    @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.CONFIRM_PASSWORD.NOT_BLANK")
    @Size(
            min = 10,
            max = 50,
            message = "VALIDATION.CHANGE_PASSWORD.CONFIRM_PASSWORD.SIZE"
    )
    @Schema(example = "<NEW_PASSWORD>")
    private String confirmPassword;
}
