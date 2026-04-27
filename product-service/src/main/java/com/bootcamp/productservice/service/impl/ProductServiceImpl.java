package com.bootcamp.productservice.service.impl;

import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.entity.PokemonCardEntity;
import com.bootcamp.productservice.exception.DataNotFoundException;
import com.bootcamp.productservice.repository.PokemonCardRepository;
import com.bootcamp.productservice.rest.PokemonClient;
import com.bootcamp.productservice.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
            log.error("Berhasil memproses data Pokemon!");
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

    private PokemonCardEntity mapToEntity(JsonNode node){
        PokemonCardEntity card = new PokemonCardEntity();
        card.setId(node.get("id").asText());
        card.setName(node.get("name").asText());
        card.setRarity(node.get("rarity").asText("common"));
        card.setRawData(node);
        return card;
    }


}
