package com.bootcamp.scheduler_service.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/product-service/api/products/sync")
    void syncPokemonData();
}
