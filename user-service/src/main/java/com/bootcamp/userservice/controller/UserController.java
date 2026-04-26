package com.bootcamp.userservice.controller;

import com.bootcamp.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp.userservice.dto.request.ReqUpdateUserFirstNameDto;
import com.bootcamp.userservice.dto.response.BaseResponse;
import com.bootcamp.userservice.dto.response.ResCreateUserDto;
import com.bootcamp.userservice.dto.response.ResFeignPokemonDetailDto;
import com.bootcamp.userservice.entity.UserEntity;
import com.bootcamp.userservice.service.ProductService;
import com.bootcamp.userservice.service.UserService;
import jakarta.validation.Valid;
import org.hibernate.mapping.Any;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ProductService productService;



    public UserController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ResCreateUserDto>> createUser(
            @Valid @RequestBody ReqCreateUserDto request
            ){
        UserEntity user = userService.createUser(request);
        ResCreateUserDto responseData = new ResCreateUserDto(
                user.getFirstName() + " " + user.getLastName()
        );
        return ResponseEntity.ok(BaseResponse.success(responseData, "Success create user"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> getUserById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long loggedInUserId
    ){
        if (!id.equals(loggedInUserId)) {
            throw new RuntimeException("Akses ditolak! Kamu tidak bisa melihat data milik orang lain.");
        }

        ResCreateUserDto respCreateUser = userService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success(respCreateUser, "Success get data by id"));
    }


    @GetMapping("/firstname")
    public ResponseEntity<BaseResponse<List<ResCreateUserDto>>> getByFirstName(
            @RequestParam String firstName
    ){
        List<ResCreateUserDto> respCreateUser = userService.getByFirstName(firstName);
        return ResponseEntity.ok(BaseResponse.success(respCreateUser, "Success get data by first name"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Any>> deleteById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long loggedInUserId
    ){
        if (!id.equals(loggedInUserId)) {
            throw new RuntimeException("Akses ditolak! Kamu tidak bisa menghapus akun orang lain.");
        }

        userService.deleteById(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Success delete data"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreateUserDto>> updateById(
            @PathVariable Long id,
            @RequestBody ReqUpdateUserFirstNameDto request,
            @RequestHeader("X-User-Id") Long loggedInUserId
    ){
        if (!id.equals(loggedInUserId)) {
            throw new RuntimeException("Akses ditolak! Kamu tidak bisa mengubah data orang lain.");
        }

        BaseResponse<ResCreateUserDto> response = userService.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<BaseResponse<ResFeignPokemonDetailDto>> getDetailById(
        @PathVariable String id
    ){
        ResFeignPokemonDetailDto resFeignPokemonDetailDto = productService.getPokemonDetailById(id);
        return ResponseEntity.ok(BaseResponse.success(resFeignPokemonDetailDto, "Success get detail data by id"));
    }
}
