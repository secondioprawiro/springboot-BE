package com.bootcamp.trade_service.service;

import com.bootcamp.trade_service.dto.request.TradeRequestDto;

public interface TradeService {
    void exchangePokemon(String requesterToken, TradeRequestDto request);
}