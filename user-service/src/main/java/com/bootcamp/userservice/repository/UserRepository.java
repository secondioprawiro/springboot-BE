package com.bootcamp.userservice.repository;

import com.bootcamp.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String username);
    Boolean existsByEmail(String email);
    @Query(
            value = """
                SELECT * from mst_user
                where first_name = :firstName
            """, nativeQuery = true
    )
    List<UserEntity> findByFirstNameNative(String firstName);

    @Query(
            """
            select u from UserEntity u where
            u.firstName = :firstName
            """
    )
    List<UserEntity> findAllByFirstNameJPQL(String firstName);
}
