package com.bootcamp.scheduler_service.scheduler;

import com.bootcamp.scheduler_service.rest.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonSyncScheduler {

    private final ProductServiceClient productServiceClient;

    @Scheduled(cron = "0 0 0 * * *")
    public void syncPokemonData() {
        log.info("Starting scheduled Pokemon data sync...");
        productServiceClient.syncPokemonData();
        log.info("Pokemon data sync triggered successfully.");
    }
}
