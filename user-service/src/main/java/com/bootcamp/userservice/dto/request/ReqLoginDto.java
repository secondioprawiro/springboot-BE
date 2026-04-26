package com.bootcamp.userservice.dto.request;

import lombok.Data;

@Data
public class ReqLoginDto {
    private String email;
    private String password;
}
