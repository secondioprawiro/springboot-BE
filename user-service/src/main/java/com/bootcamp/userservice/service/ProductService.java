package com.bootcamp.userservice.service;

import com.bootcamp.userservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.userservice.dto.response.ResFeignPokemonDetailDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    ResCreatePokemonDto getPokemonById(String id);
    ResFeignPokemonDetailDto getPokemonDetailById(String id);
}
