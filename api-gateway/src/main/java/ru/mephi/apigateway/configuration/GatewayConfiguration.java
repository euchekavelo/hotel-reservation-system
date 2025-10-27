package ru.mephi.apigateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mephi.apigateway.filter.CustomGatewayFilter;

@Configuration
public class GatewayConfiguration {

    private final CustomGatewayFilter customGatewayFilter;

    @Autowired
    public GatewayConfiguration(CustomGatewayFilter customGatewayFilter) {
        this.customGatewayFilter = customGatewayFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("booking_service_route", r -> r.path("/booking-service/**")
                        .filters(f -> f.filter(customGatewayFilter))
                        .uri("lb://BOOKING-SERVICE")
                )
                .route("hotel_management_service_route", r -> r.path("/hotel-management-service/**")
                        .filters(f -> f.filter(customGatewayFilter))
                        .uri("lb://HOTEL-MANAGEMENT-SERVICE")
                )
                .build();
    }
}
