package com.bootcamp.gateway_service.config;

import com.bootcamp.gateway_service.response.BaseResponse;
import com.bootcamp.gateway_service.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

//        String apiKey = request.getHeaders().getFirst("APIKey");
//
//        if(apiKey == null || !apiKey.equals(validApiKey)){
//            return unauthorizedResponse
//        }

        if(isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        //extract user info
        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);

        // tambahkan header untuk downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Id", String.valueOf(userId))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.emptyList()
        );

        Mono<Void> filter = chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        return filter;
    }

    private boolean isPublicEndpoint (String path){
        return path.contains("/api/auth/login")
                || path.contains("/api/auth")
                || path.contains("/actuator");
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        BaseResponse<Void> errorResponse = BaseResponse.failed(null, message);

        try {
            String json = mapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes());
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            String fallback = "{\"status\": \"F\", \"message\": \"" + message + "\\}";
            DataBuffer buffer = response.bufferFactory().wrap(fallback.getBytes());
            return response.writeWith(Mono.just(buffer));
        }
    }

}
