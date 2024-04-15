package com.example.cloud_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDTO {
  @NotBlank(message = "Enter your current password")
  private String currentPassword;
  @NotBlank(message = "Enter your new password")
  private String newPassword;
  @NotBlank(message = "Confirm your new password")
  private String confirmNewPassword;
}
