package com.bootcamp.productservice.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user-service"
)
public interface UserClient {
    @GetMapping("/user-service/api/users/{id}")
    Object getUserById(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long userId
    );

}
