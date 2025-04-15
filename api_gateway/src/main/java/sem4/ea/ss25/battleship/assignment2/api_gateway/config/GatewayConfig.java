package sem4.ea.ss25.battleship.assignment2.api_gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/game/**")
                        .uri("lb://game-service"))
                .route(r -> r.path("/api/board/**")
                        .uri("lb://board-service"))
                .route(r -> r.path("/api/player/**")
                        .uri("lb://player-service"))
                .route(r -> r.path("/swagger-ui/**")
                        .uri("http://localhost:8080"))
                .route(r -> r.path("/v3/api-docs/**")
                        .uri("http://localhost:8080"))
                .build();
    }
}
