package com.bootcamp.productservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ClaimStarterRequest {
    @NotEmpty(message = "Minimal claim 1 pokemon dan tidak boleh kosong")
    @Size(max = 3, message = "Maksimal claim 3 pokemon")
    private List<String> pokemonIds;
}
