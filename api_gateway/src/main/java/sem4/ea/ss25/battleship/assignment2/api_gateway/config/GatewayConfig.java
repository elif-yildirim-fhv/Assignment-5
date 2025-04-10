package sem4.ea.ss25.battleship.assignment2.api_gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Game Service Routes
                .route("game-service", r -> r.path("/api/game/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("gameServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/game")))
                        .uri("lb://game-service"))
                
                // Board Service Routes
                .route("board-service", r -> r.path("/api/board/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("boardServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/board")))
                        .uri("lb://board-service"))
                
                // Player Service Routes
                .route("player-service", r -> r.path("/api/player/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("playerServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/player")))
                        .uri("lb://player-service"))
                
                // Swagger UI route
                .route("openapi", r -> r.path("/v3/api-docs/**")
                        .uri("lb://api-gateway"))
                .route("swagger-ui", r -> r.path("/swagger-ui/**")
                        .uri("lb://api-gateway"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(5000))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig)
                .build());
    }
}
