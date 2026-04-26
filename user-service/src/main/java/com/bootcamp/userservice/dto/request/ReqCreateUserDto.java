package com.bootcamp.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqCreateUserDto {
    @NotBlank(message = "first name wajib diisi woy!")
    private String firstName;

    @NotBlank(message = "last name wajib diisi jing!")
    private String lastName;

    @NotBlank(message = "email wajib diisi kocak!")
    private String email;

    @NotBlank(message = "first name wajib diisi")
    private String phoneNumber;

    @NotBlank(message = "password wajib diisi")
    private String password;

}
