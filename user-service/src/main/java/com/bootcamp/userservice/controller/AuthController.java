package com.bootcamp.userservice.controller;

import com.bootcamp.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp.userservice.dto.request.ReqLoginDto;
import com.bootcamp.userservice.entity.UserEntity;
import com.bootcamp.userservice.service.UserService;
import com.bootcamp.userservice.util.JwtUtil;
import com.bootcamp.userservice.util.ResponseJwt;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;


    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseJwt> registerUser(
            @Valid @RequestBody ReqCreateUserDto request
    ){
        UserEntity user = userService.register(request);

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        ResponseJwt responseData = new ResponseJwt(
                user.getEmail(),
                token
        );
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseJwt> login(
            @Valid @RequestBody ReqLoginDto request
    ){
        ResponseJwt response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
