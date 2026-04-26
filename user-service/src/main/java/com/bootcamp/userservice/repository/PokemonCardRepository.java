package com.bootcamp.userservice.repository;

import com.bootcamp.userservice.entity.PokemonCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonCardRepository extends JpaRepository<PokemonCardEntity, String> {
}

