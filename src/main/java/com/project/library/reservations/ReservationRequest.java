package com.project.library.reservations;

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
public class ReservationRequest {
    @Email(message = "Needs to be a valid email")
    @NotBlank(message = "Can not be empty")
    private String email;

    @NotNull(message = "Can not be empty")
    private Long book;
}
