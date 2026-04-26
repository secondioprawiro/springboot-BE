package com.bootcamp.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResFeignPokemonDetailDto {
    private String id;
    private String name;
    private String rarity;
}
