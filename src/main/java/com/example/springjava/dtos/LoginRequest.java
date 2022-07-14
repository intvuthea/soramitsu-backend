package com.example.springjava.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoginRequest {
    @NotNull(message = "username is required")
    private String username;

    @NotNull(message = "password is required")
    private String password;
}
