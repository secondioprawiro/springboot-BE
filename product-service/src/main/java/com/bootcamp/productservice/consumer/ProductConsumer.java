package com.bootcamp.productservice.consumer;

import com.bootcamp.productservice.dto.message.ProductViewMessage;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.service.ProductService;
import com.bootcamp.productservice.util.RedisKeyHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductConsumer {
    private final RedisKeyHelper redisKeyHelper;
    private final StringRedisTemplate stringRedisTemplate;

    @KafkaListener(
            id = "PRODUCT_VIEW",
            topics = "PRODUCT_VIEW",
            containerFactory = "productViewMessageConcurrentKafkaListenerContainerFactory",
            autoStartup = "true"
    )
    public void processProductView(ProductViewMessage message) {
        String trendingKey = redisKeyHelper.generateKey("pokemon:trending", "global");
        stringRedisTemplate.opsForZSet().incrementScore(trendingKey, message.getPokemonId(),1);

        if(message.getUserId() != null){
            String historyKey = redisKeyHelper.generateKey("user:history", "global");

            stringRedisTemplate.opsForList().remove(historyKey, 0, message.getUserId());
            stringRedisTemplate.opsForList().leftPush(historyKey, message.getPokemonId());

            stringRedisTemplate.opsForList().trim(historyKey, 0, 4);
        }

        log.info("Done Processing pokemon trend and user history");
    }
}
