package com.project.library.security.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPasswordRequest {
    @NotBlank(message = "Can not be empty")
    @Email(message = "Needs to be a valid email")
    private String email;
    @NotBlank(message = "Can not be empty")
    private String password;
    @NotBlank(message = "Can not be empty")
    private String repeat;
    @NotBlank(message = "Can not be empty")
    private String otp;
}
