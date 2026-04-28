package com.bootcamp.trade_service.service.impl;

import com.bootcamp.trade_service.dto.request.TradeRequestDto;
import com.bootcamp.trade_service.entity.PokemonCardEntity;
import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import com.bootcamp.trade_service.entity.UserPokemonEntity;
import com.bootcamp.trade_service.repository.PokemonCardRepository;
import com.bootcamp.trade_service.repository.TradeHistoryRepository;
import com.bootcamp.trade_service.repository.UserPokemonRepository;
import com.bootcamp.trade_service.rest.UserClient;
import com.bootcamp.trade_service.service.TradeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final UserPokemonRepository userPokemonRepository;
    private final PokemonCardRepository pokemonCardRepository;
    private final TradeHistoryRepository tradeHistoryRepository;
    private final UserClient userClient;

    @Override
    @Transactional
    public void exchangePokemon(String requesterToken, TradeRequestDto request) {
        Long requesterId = Long.parseLong(requesterToken);
        Long receiverId = request.getReceiverId();

        // validasi tidak boleh trade dengan diri sendiri
        if (requesterId.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tidak boleh trade dengan diri sendiri");
        }

        // validasi user receiver (panggil user-service via Feign)
        try {
            userClient.getUserById(receiverId, receiverId);
        } catch (Exception e) {
            log.error("Error validasi user: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User receiver tidak ditemukan");
        }

        // Cek master data pokemon
        PokemonCardEntity myPokemon = pokemonCardRepository.findById(request.getMyPokemonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pokemon milikmu tidak ditemukan"));

        PokemonCardEntity receiverPokemon = pokemonCardRepository.findById(request.getReceiverPokemonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pokemon target tidak ditemukan"));

        // Validasi rarity harus sama
        if (!myPokemon.getRarity().equalsIgnoreCase(receiverPokemon.getRarity())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rarity tidak sama, trade tidak dapat dilakukan");
        }

        // Validasi kepemilikan pokemon requester
        UserPokemonEntity myOwnership = userPokemonRepository.findByUserIdAndPokemonId(requesterId, request.getMyPokemonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anda tidak memiliki pokemon ini"));

        // Validasi kepemilikan pokemon receiver
        UserPokemonEntity receiverOwnership = userPokemonRepository.findByUserIdAndPokemonId(receiverId, request.getReceiverPokemonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver tidak memiliki pokemon tersebut"));

        // Transfer (Delete lalu Insert)
        userPokemonRepository.delete(myOwnership);
        userPokemonRepository.delete(receiverOwnership);

        userPokemonRepository.save(UserPokemonEntity.builder()
                .userId(requesterId)
                .pokemonId(request.getReceiverPokemonId())
                .build());

        userPokemonRepository.save(UserPokemonEntity.builder()
                .userId(receiverId)
                .pokemonId(request.getMyPokemonId())
                .build());

        // Simpan ke History
        tradeHistoryRepository.save(TradeHistoryEntity.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .requesterPokemonId(request.getMyPokemonId())
                .receiverPokemonId(request.getReceiverPokemonId())
                .build());
    }
}