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
                // Game Service Routes
                .route("game-service", r -> r.path("/api/game/**")
                        .uri("lb://game-service"))
                
                // Board Service Routes
                .route("board-service", r -> r.path("/api/board/**")
                        .uri("lb://board-service"))
                
                // Player Service Routes
                .route("player-service", r -> r.path("/api/player/**")
                        .uri("lb://player-service"))
                
                // Swagger UI routes
                .route("openapi", r -> r.path("/v3/api-docs/**")
                        .uri("http://localhost:8080"))
                .route("swagger-ui", r -> r.path("/swagger-ui/**")
                        .uri("http://localhost:8080"))
                .build();
    }
}
