package com.bootcamp.trade_service.repository;

import com.bootcamp.trade_service.entity.PokemonCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonCardRepository extends JpaRepository<PokemonCardEntity, String> {
}
