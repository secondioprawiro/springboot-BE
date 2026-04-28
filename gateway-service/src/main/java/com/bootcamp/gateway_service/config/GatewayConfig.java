package com.bootcamp.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("user-service", r -> r
                        .path("/gateway/user-service/**")
                        .filters(f -> {
                            f.rewritePath(
                                    "/gateway/user-service/(?<segment>.*)",
                                    "/user-service/${segment}"
                            );
                            return f;
                        }).uri("lb://user-service")
                )
                .route("product-service", r -> r
                        .path("/gateway/product-service/**")
                        .filters(f -> {
                            f.rewritePath(
                                    "/gateway/product-service/(?<segment>.*)",
                                    "/product-service/${segment}"
                            );
                            return f;
                        }).uri("lb://product-service")
                )
                .route("trade-service", r -> r
                        .path("/gateway/trade-service/**")
                        .filters(f -> {
                            f.rewritePath(
                                    "/gateway/trade-service/(?<segment>.*)",
                                    "/trade-service/${segment}"
                            );
                            return f;
                        }).uri("lb://trade-service")
                )
                .build();
    }
}
