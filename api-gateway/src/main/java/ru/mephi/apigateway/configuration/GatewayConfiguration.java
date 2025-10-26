package ru.mephi.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("booking_service_route", r -> r.path("/booking-service/**")
                        .uri("lb://BOOKING-SERVICE")
                )
                .route("hotel_management_service_route", r -> r.path("/hotel-management-service/**")
                        .uri("lb://HOTEL-MANAGEMENT-SERVICE")
                )
                .build();
    }
}
