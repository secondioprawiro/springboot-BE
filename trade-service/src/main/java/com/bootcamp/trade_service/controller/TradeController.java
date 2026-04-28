package com.bootcamp.trade_service.controller;

import com.bootcamp.trade_service.dto.request.TradeRequestDto;
import com.bootcamp.trade_service.dto.response.BaseResponse;
import com.bootcamp.trade_service.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> exchangePokemon(
            @RequestHeader("X-User-Id") String requesterId,
            @Valid @RequestBody TradeRequestDto request
    ) {
        tradeService.exchangePokemon(requesterId, request);
        return ResponseEntity.ok(BaseResponse.success(null, "Trade berhasil dilakukan"));
    }
}