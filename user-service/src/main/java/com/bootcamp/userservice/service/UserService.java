package com.bootcamp.userservice.service;

import com.bootcamp.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp.userservice.dto.request.ReqLoginDto;
import com.bootcamp.userservice.dto.request.ReqUpdateUserFirstNameDto;
import com.bootcamp.userservice.dto.response.BaseResponse;
import com.bootcamp.userservice.dto.response.ResCreateUserDto;
import com.bootcamp.userservice.entity.UserEntity;
import com.bootcamp.userservice.util.ResponseJwt;

import java.util.List;

public interface UserService {
    UserEntity createUser(ReqCreateUserDto request);
    ResCreateUserDto getUserById(Long id);
    List<ResCreateUserDto> getByFirstName(String firstName);
    void deleteById(Long id);
    BaseResponse<ResCreateUserDto> updateById (Long id, ReqUpdateUserFirstNameDto request);
    UserEntity register(ReqCreateUserDto request);
    ResponseJwt login(ReqLoginDto request);

}
