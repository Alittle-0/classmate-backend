package com.devteam.identityservice.dto.request;

import com.devteam.identityservice.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
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

    @NotNull(message = "VALIDATION.UPDATE_INFORMATION.ROLE.NOT_NULL")
    @Schema(example = "STUDENT")
    private Role role;

}
