package com.bootcamp.userservice.service.impl;

import com.bootcamp.userservice.dto.response.BaseResponse;
import com.bootcamp.userservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.userservice.dto.response.ResFeignPokemonDetailDto;
import com.bootcamp.userservice.entity.PokemonCardEntity;
import com.bootcamp.userservice.exception.DataNotFoundException;
import com.bootcamp.userservice.repository.PokemonCardRepository;
import com.bootcamp.userservice.rest.PokemonClient;
import com.bootcamp.userservice.service.ProductService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final PokemonCardRepository pokemonCardRepository;
    private final PokemonClient pokemonClient;

    @Override
    public ResCreatePokemonDto getPokemonById(String id) {
        Optional<PokemonCardEntity> pokemonCardEntity = pokemonCardRepository.findById(id);
        if(pokemonCardEntity.isEmpty()){
            throw new DataNotFoundException("Pokemon Not Found");
        }

        PokemonCardEntity pokemonCard = pokemonCardEntity.get();
        return new ResCreatePokemonDto(
                pokemonCard.getId(), pokemonCard.getName(), pokemonCard.getRarity()
        );
    }

    public ResFeignPokemonDetailDto getPokemonDetailById(String id) {
        try {
            BaseResponse<ResFeignPokemonDetailDto> response =
                    pokemonClient.getDetailPokemon(id);
            return response.getData();
        }catch (FeignException.NotFound ex){
            throw new DataNotFoundException("Pokemon Not Found");
        }

    }



}
