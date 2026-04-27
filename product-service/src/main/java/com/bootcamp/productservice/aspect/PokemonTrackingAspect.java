package com.bootcamp.productservice.aspect;

import com.bootcamp.productservice.dto.message.ProductViewMessage;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.producer.KafkaProducer;
import com.bootcamp.productservice.util.DateHelper;
import com.bootcamp.productservice.util.RedisKeyHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
@Order(1)
public class PokemonTrackingAspect {

//    private final RedisKeyHelper redisKeyHelper;
//    private final StringRedisTemplate stringRedisTemplate;

    private final KafkaProducer<ProductViewMessage> kafkaProducer;

    @AfterReturning(
            pointcut = ("execution(* com.bootcamp.productservice.service.impl.ProductServiceImpl.getPokemonById(..))"),
            returning = "result"
    )
    public void trackTrending(JoinPoint joinPoint, Object result) {

        if(result instanceof ResCreatePokemonDto){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Integer userId = null;

            if(Objects.nonNull(attributes)){
                HttpServletRequest request = attributes.getRequest();
                String userIdHeader = request.getHeader("X-User-Id");
                if (Objects.nonNull(userIdHeader)) {
                    userId = Integer.parseInt(userIdHeader);
                }
            }

            ProductViewMessage productViewMessage = new ProductViewMessage(
                    userId,
                    ((ResCreatePokemonDto) result).getId(),
                    DateHelper.now()
            );

            kafkaProducer.sendMessage("PRODUCT_VIEW", productViewMessage);

//            String trendingKey = redisKeyHelper.generateKey("pokemon:trending", "global");
//            stringRedisTemplate.opsForZSet().incrementScore(trendingKey, ((ResCreatePokemonDto) result).getId(),1);
        }
    }
}
