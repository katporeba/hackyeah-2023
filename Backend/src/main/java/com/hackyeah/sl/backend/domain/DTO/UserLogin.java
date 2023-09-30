package com.hackyeah.sl.backend.domain.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserLogin {

  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email format is not valid")
  private String email;

  private String password;
}
