package com.bootcamp.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqUpdateUserFirstNameDto {
    @NotBlank(message = "first name wajib di isi")
    private String firstName;
}
