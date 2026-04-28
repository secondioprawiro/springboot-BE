package com.bootcamp.productservice.service.impl;

import com.bootcamp.productservice.dto.request.ClaimStarterRequest;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.entity.PokemonCardEntity;
import com.bootcamp.productservice.entity.UserPokemonEntity;
import com.bootcamp.productservice.exception.DataNotFoundException;
import com.bootcamp.productservice.repository.PokemonCardRepository;
import com.bootcamp.productservice.repository.UserPokemonRepository;
import com.bootcamp.productservice.rest.PokemonClient;
import com.bootcamp.productservice.rest.UserClient;
import com.bootcamp.productservice.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final PokemonCardRepository pokemonCardRepository;
    private final PokemonClient pokemonClient;
    private final UserPokemonRepository userPokemonRepository;
    private final UserClient userClient;

    @CacheEvict(value = "pokemon:detail", allEntries = true)
    @Async("pokemonTaskExecutor")
    @Override
    public CompletableFuture<String> syncDataByThirdPartyApi(){
        try{
            JsonNode response = pokemonClient.searchCards("");
            JsonNode cardList = response.get("data");

            if(cardList == null && !cardList.isArray()){
                return CompletableFuture.completedFuture("Gagal: Data tidak ditemukan");
            }

            List<PokemonCardEntity> cards = new ArrayList<>();
            for (JsonNode node : cardList) {
                cards.add(mapToEntity(node));
            }

            pokemonCardRepository.saveAll(cards);

            log.info("Berhasil memproses data Pokemon!");

            return CompletableFuture.completedFuture("Sync Success: " + cards.size() + " cards saved");
        }catch (Exception e){
            log.error("Berhasil memproses data Pokemon!", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Cacheable(
            value = "pokemon:detail", key = "#p0"
    )
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

    @Override
    public List<ResCreatePokemonDto> claimStarter(String authHeader, ClaimStarterRequest request) {
        Long userId = Long.parseLong(authHeader);

        if (userPokemonRepository.countByUserId(userId) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User sudah pernah claim");
        }

        List<PokemonCardEntity> validPokemons = pokemonCardRepository.findAllById(request.getPokemonIds());

        if (validPokemons.isEmpty()) {
            throw new DataNotFoundException("Pokemon tidak ditemukan");
        }

        List<UserPokemonEntity> claimsToSave = validPokemons.stream()
                .map(pokemon -> UserPokemonEntity.builder()
                        .userId(userId)
                        .pokemonId(pokemon.getId())
                        .build())
                .toList();

        userPokemonRepository.saveAll(claimsToSave);

        return validPokemons.stream()
                .map(pokemon -> new ResCreatePokemonDto(pokemon.getId(), pokemon.getName(), pokemon.getRarity()))
                .toList();
    }

    @Override
    public List<ResCreatePokemonDto> getMyPokemon(String token, String rarity) {
        Long userId = Long.parseLong(token);

        List<UserPokemonEntity> userPokemons = userPokemonRepository.findByUserId(userId);

        if (userPokemons.isEmpty()) {
            return new ArrayList<>();
        }

        // ambil semua pokemon_id yang dimiliki user
        List<String> myPokemonIds = userPokemons.stream()
                .map(UserPokemonEntity::getPokemonId)
                .toList();

        // ambil pokemon detail
        List<PokemonCardEntity> myPokemonDetails = pokemonCardRepository.findAllById(myPokemonIds);

        // filter by rarity
        if (rarity != null && !rarity.trim().isEmpty()) {
            myPokemonDetails = myPokemonDetails.stream()
                    .filter(pokemon -> pokemon.getRarity() != null &&
                            pokemon.getRarity().equalsIgnoreCase(rarity))
                    .toList();
        }

        // mapping
        return myPokemonDetails.stream()
                .map(pokemon -> new ResCreatePokemonDto(
                        pokemon.getId(),
                        pokemon.getName(),
                        pokemon.getRarity()))
                .toList();
    }

    @Override
    public List<ResCreatePokemonDto> getPokemonByUserId(Long targetUserId) {

        try {
            userClient.getUserById(targetUserId, targetUserId);
        }catch (Exception e){
            log.error("Error saat memanggil user-service: {}", e.getMessage());
            throw new DataNotFoundException("User target dengan ID " + targetUserId + " tidak ditemukan!");
        }

        List<UserPokemonEntity> userPokemons = userPokemonRepository.findByUserId(targetUserId);

        if (userPokemons.isEmpty()) {
            return new ArrayList<>();
        }

        // ekstrak pokemon id
        List<String> targetPokemonIds = userPokemons.stream()
                .map(UserPokemonEntity::getPokemonId)
                .toList();

        // ambil target pokemon detail
        List<PokemonCardEntity> targetPokemonDetails = pokemonCardRepository.findAllById(targetPokemonIds);

        // mapping
        return targetPokemonDetails.stream()
                .map(pokemon -> new ResCreatePokemonDto(
                        pokemon.getId(),
                        pokemon.getName(),
                        pokemon.getRarity()))
                .toList();
    }

    private PokemonCardEntity mapToEntity(JsonNode node){
        PokemonCardEntity card = new PokemonCardEntity();
        card.setId(node.get("id").asText());
        card.setName(node.get("name").asText());
        card.setRarity(node.get("rarity").asText("common"));
        card.setRawData(node);
        return card;
    }


}
