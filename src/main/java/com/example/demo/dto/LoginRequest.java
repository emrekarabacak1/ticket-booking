package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email girilmelidir")
    private String email;

    @NotBlank(message = "Şifre girilmelidir")
    private String password;
}
