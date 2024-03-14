package com.example.cloud_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Provide a valid email")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;
    private String name;

}
