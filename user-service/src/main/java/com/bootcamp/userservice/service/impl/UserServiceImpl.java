package com.bootcamp.userservice.service.impl;

import com.bootcamp.userservice.dto.request.ReqCreateUserDto;
import com.bootcamp.userservice.dto.request.ReqLoginDto;
import com.bootcamp.userservice.dto.request.ReqUpdateUserFirstNameDto;
import com.bootcamp.userservice.dto.response.BaseResponse;
import com.bootcamp.userservice.dto.response.ResCreateUserDto;
import com.bootcamp.userservice.entity.UserEntity;
import com.bootcamp.userservice.exception.BadRequestException;
import com.bootcamp.userservice.exception.DataNotFoundException;
import com.bootcamp.userservice.repository.UserRepository;
import com.bootcamp.userservice.service.UserService;
import com.bootcamp.userservice.util.JwtUtil;
import com.bootcamp.userservice.util.ResponseJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(ReqCreateUserDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email sudah terdaftar");
        }
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public ResCreateUserDto getUserById(Long id) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()){
            throw new DataNotFoundException("User not found");
        }
        UserEntity user = userOpt.get();
        return new ResCreateUserDto(
                user.getFirstName() + " " + user.getLastName()
        );
    }

    @Override
    public List<ResCreateUserDto> getByFirstName(String firstName) {
        List<UserEntity> user = userRepository.findByFirstNameNative(firstName);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        List<ResCreateUserDto> userEntity = user.stream()
                .map(userfirst -> new ResCreateUserDto(userfirst.getFirstName())).toList();
        return userEntity;
    }

    @Override
    public void deleteById(Long id) {
        if(!userRepository.existsById(id)) {
            throw new BadRequestException("Id tidak ditemukan");
        }

        userRepository.deleteById(id);
    }

    @Override
    public BaseResponse<ResCreateUserDto> updateById(Long id, ReqUpdateUserFirstNameDto request) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("User not found")
        );

        user.setFirstName(request.getFirstName());
        userRepository.save(user);

        BaseResponse<ResCreateUserDto> response = new BaseResponse<>();
        response.setData(new ResCreateUserDto(user.getFirstName() + " " + user.getLastName()));

        return response;
    }

    @Override
    public UserEntity register(ReqCreateUserDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email sudah terdaftar");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public ResponseJwt login(ReqLoginDto request) {

        System.out.println("Email yang ditangkap dari Postman: '" + request.getEmail() + "'");

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password salah");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new ResponseJwt(
                user.getEmail(),
                token
        );

    }


}
