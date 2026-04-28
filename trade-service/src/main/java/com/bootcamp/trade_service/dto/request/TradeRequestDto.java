package com.bootcamp.trade_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradeRequestDto {
    @NotNull(message = "Receiver ID tidak boleh kosong")
    private Long receiverId;

    @NotBlank(message = "My Pokemon ID tidak boleh kosong")
    private String myPokemonId;

    @NotBlank(message = "Receiver Pokemon ID tidak boleh kosong")
    private String receiverPokemonId;
}