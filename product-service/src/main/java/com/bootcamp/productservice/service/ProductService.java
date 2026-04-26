package com.bootcamp.productservice.service;

import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ProductService {
    CompletableFuture<String> syncDataByThirdPartyApi();
    ResCreatePokemonDto getPokemonById(String id);
}
