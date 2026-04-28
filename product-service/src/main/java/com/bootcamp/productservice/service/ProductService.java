package com.bootcamp.productservice.service;

import com.bootcamp.productservice.dto.request.ClaimStarterRequest;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<String> syncDataByThirdPartyApi();
    ResCreatePokemonDto getPokemonById(String id);
    List<ResCreatePokemonDto> claimStarter(String authHeader, ClaimStarterRequest request);
    List<ResCreatePokemonDto> getMyPokemon(String token, String rarity);
    List<ResCreatePokemonDto> getPokemonByUserId(Long targetUserId);
}
