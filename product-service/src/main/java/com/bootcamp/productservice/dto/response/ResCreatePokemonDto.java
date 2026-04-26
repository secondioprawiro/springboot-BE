package com.bootcamp.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResCreatePokemonDto {
    private String id;
    private String name;
    private String rarity;
}
