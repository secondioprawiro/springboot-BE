package com.bootcamp.productservice.repository;

import com.bootcamp.productservice.entity.UserPokemonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPokemonRepository extends JpaRepository<UserPokemonEntity, Long> {
    long countByUserId(long userId);
    List<UserPokemonEntity> findByUserId(Long userId);
}
