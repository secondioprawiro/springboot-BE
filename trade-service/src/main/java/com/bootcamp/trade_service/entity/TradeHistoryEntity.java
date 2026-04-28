package com.bootcamp.trade_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requesterId;
    private Long receiverId;

    private String requesterPokemonId;
    private String receiverPokemonId;

    @Builder.Default
    private String status = "Completed";

    @CreationTimestamp
    private LocalDateTime tradedAt;
}