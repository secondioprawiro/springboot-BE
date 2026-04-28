package com.bootcamp.trade_service.repository;

import com.bootcamp.trade_service.entity.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistoryEntity, Long> {
}