package com.hackyeah.sl.backend.domain.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserRegister {
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email format is not valid")
  private String email;

  @NotBlank(message = "Password is mandatory")
  @Size(min = 8, max = 30)
  private String password;

  @NotBlank(message = "Repeat password is mandatory")
  @Size(min = 8, max = 30)
  private String repeatPassword;

}
