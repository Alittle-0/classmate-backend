package com.devteam.identityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileUpdateRequestDTO {

    @NotBlank(message = "VALIDATION.UPDATE_INFORMATION.FIRSTNAME.NOT_BLANK")
    @Size(
            min = 2,
            max = 50,
            message = "VALIDATION.UPDATE_INFORMATION.FIRSTNAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$"
    )
    @Schema(example = "Michael")
    private String firstname;

    @NotBlank(message = "VALIDATION.UPDATE_INFORMATION.LASTNAME.NOT_BLANK")
    @Size(
            min = 2,
            max = 10,
            message = "VALIDATION.UPDATE_INFORMATION.LASTNAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$"
    )
    @Schema(example = "Dang")
    private String lastname;

    @NotBlank(message = "VALIDATION.UPDATE_INFORMATION.EMAIL.NOT_BLANK")
    @Email(message = "VALIDATION.UPDATE_INFORMATION.EMAIL.FORMAT")
    @Schema(example = "abc@gmail.com")
    private String email;

}
