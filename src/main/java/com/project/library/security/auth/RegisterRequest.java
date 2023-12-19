package com.project.library.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "First name can not be empty")
    private String firstname;
    private String lastname;
    @Email(message = "needs to be a valid email")
    private String email;
    @NotBlank(message = "Password can not be empty")
    private String password;


}
