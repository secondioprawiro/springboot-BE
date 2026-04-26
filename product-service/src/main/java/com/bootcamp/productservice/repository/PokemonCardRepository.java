package com.bootcamp.productservice.repository;

import com.bootcamp.productservice.entity.PokemonCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonCardRepository extends JpaRepository<PokemonCardEntity, String> {
}
