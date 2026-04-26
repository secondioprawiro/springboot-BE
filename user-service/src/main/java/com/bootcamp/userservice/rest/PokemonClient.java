package com.bootcamp.userservice.rest;

import com.bootcamp.userservice.dto.response.ResFeignPokemonDetailDto;
import com.bootcamp.userservice.dto.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "pokemonClient", url = "https://api.pokemontcg.io/v2")
//public interface PokemonClient {
//    @GetMapping("/cards")
//    JsonNode searchCards(@RequestParam("q") String query);
//}

@FeignClient(
        name = "pokemon-service",
        path = "/pokemon-service"
)
public interface PokemonClient {
    @GetMapping("/api/products/{id}")
    BaseResponse<ResFeignPokemonDetailDto> getDetailPokemon(
            @PathVariable String id
    );
}